package kr.co.seoulit.his.billingservice.charge.service;

import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.billing.repository.BillingRepository;
import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;
import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeResponseDTO;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.master.entity.BillingMasterEntity;
import kr.co.seoulit.his.billingservice.master.repository.BillingMasterRepository;
import kr.co.seoulit.his.billingservice.master.repository.CommonCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BillingChargeServiceImpl implements BillingChargeService {

    private final BillingMasterRepository billingMasterRepository;// 수납기준정보(billing_master) 조회용
    private final BillingDetailRepository billingDetailRepository;// 수납상세정보(billing_detail) insert용
    private final BillingRepository billingRepository;            // 수납헤더정보(billing) 조회/insert/update용
    private final CommonCodeRepository commonCodeRepository;      // ADMIN.COMMON_CODE 조회 전용

    // 공통코드 "서비스구분" 그룹(외래시스템/응급시스템/병동시스템/검사시스템/수술시스템) - admin.common_code.group_id
    @Value("${billing.master.source-service-code.group-id}")
    private String sourceServiceCodeGroupId;

    // 공통코드 "급여구분코드" 그룹 중 "비급여(NON_INS)" 코드의 CODE_ID.
    // billing_master.insurance_type_code가 공통코드 CODE_ID(UUID)로 바뀌면서 문자열 "NON_INS" 비교가
    // 더 이상 성립하지 않게 되어, 실제 CODE_ID 값을 프로퍼티로 주입받아 비교한다.
    @Value("${billing.charge.insurance-type.non-ins-code-id}")
    private String nonInsCodeId;

    @Override
    public void createCharge(BillingChargeRequestDTO billingChargeRequestDTO) {
        String receptionId = billingChargeRequestDTO.getReceptionId();   //접수 id get
        String admissionId = billingChargeRequestDTO.getAdmissionId();   //입원 id get

        if (receptionId == null && admissionId == null) {
            throw new BusinessException(ErrorCode.BILLING_RECEPTION_OR_ADMISSION_ID_REQUIRED);
        } //접수id 입원id 둘다없으면 오류

        // 타 서비스가 보낸 sourceServiceCode가 실제로 admin의 공통코드("서비스구분" 그룹)에 등록된 값인지 검증
        if (billingChargeRequestDTO.getSourceServiceCode() == null// sourceServiceCode가 null이거나 공백이거나, 공통코드에 존재하지 않으면 오류
                || billingChargeRequestDTO.getSourceServiceCode().isBlank()
                || !commonCodeRepository.existsByCodeIdAndGroupIdAndUseYn(
                        billingChargeRequestDTO.getSourceServiceCode(), sourceServiceCodeGroupId, "Y")) {
                            // sourceServiceCode가 공통코드에 존재하지 않으면 오류
            throw new BusinessException(ErrorCode.BILLING_SOURCE_SERVICE_CODE_NOT_FOUND);
        }

        // feeCode로 수납기준정보(billing_master)를 조회해 billingMasterId를 확보
        BillingMasterEntity billingMaster = billingMasterRepository.findByFeeCode(billingChargeRequestDTO.getFeeCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND));

        // amount는 호출자가 보낸 값을 믿지 않고 DB 프로시저(calc_charge_amount)로 계산한다: 기본단가 × quantity(입원일수).
        // (병동은 수가코드와 입원일수만 보내고, 호출자가 amount를 0으로 보내거나 안 보내는 경우도 있어서 그대로 믿으면 안 됨)
        Map<String, Object> calcParams = new HashMap<>();
        calcParams.put("fee_code", billingChargeRequestDTO.getFeeCode());
        calcParams.put("quantity", new BigDecimal(billingChargeRequestDTO.getQuantity()));
        try {
            billingDetailRepository.calcChargeAmount(calcParams);
        } catch (DataAccessException e) {
            // 프로시저의 RAISE_APPLICATION_ERROR 번호로 원인 구분 (-20001: 수가코드 없음, -20002: 수량 0 이하)
            String message = String.valueOf(e.getMostSpecificCause().getMessage());
            if (message.contains("ORA-20001")) {
                throw new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND);
            }
            if (message.contains("ORA-20002")) {
                throw new BusinessException(ErrorCode.BILLING_CHARGE_QUANTITY_INVALID);
            }
            throw e;
        }
        BigDecimal unitPrice = (BigDecimal) calcParams.get("unit_price");
        BigDecimal amount = (BigDecimal) calcParams.get("amount");

        // 보험/본인부담금 분리 - 실제 급여기준표 반영 전 임시 규칙:
        // 비급여(NON_INS)는 전액 본인부담, 급여(그 외)는 본인부담 30% / 보험부담 70%로 고정 계산
        BigDecimal patientAmount;
        BigDecimal insuranceAmount;
        if (nonInsCodeId.equals(billingMaster.getInsuranceTypeCode())) {
            patientAmount = amount;
            insuranceAmount = BigDecimal.ZERO;
        } else {
            patientAmount = amount.multiply(new BigDecimal("0.30"));
            insuranceAmount = amount.subtract(patientAmount); // 반올림 오차 없이 합계가 amount와 정확히 맞도록 뺄셈으로 계산
        }

        // 요청 DTO(타서비스 입력)를 내부 저장용 DTO로 변환하면서 billingMasterId를 채움
        BillingChargeResponseDTO billingCharge = BillingChargeResponseDTO.builder()
                .patientId(billingChargeRequestDTO.getPatientId())
                .receptionId(receptionId)
                .admissionId(admissionId)
                .billingType(receptionId != null ? "OUTPATIENT" : "INPATIENT")
                .sourceServiceCode(billingChargeRequestDTO.getSourceServiceCode())
                .sourceRecordId(billingChargeRequestDTO.getSourceRecordId())
                .feeCode(billingChargeRequestDTO.getFeeCode())
                .itemName(billingChargeRequestDTO.getItemName())
                .unitPrice(unitPrice.toString())
                .quantity(billingChargeRequestDTO.getQuantity())
                .amount(amount.toString())
                .billingMasterId(billingMaster.getBillingMasterId())
                .build();

        // 결제 전(READY) billing이 있으면 거기에 합산, 없으면(처음이거나 이전 건이 이미 결제됨) 새 billing 생성
        BillingEntity billing = receptionId != null
                ? billingRepository.findByReceptionIdAndBillingStatus(receptionId, "READY").orElse(null)
                : billingRepository.findByAdmissionIdAndBillingStatus(admissionId, "READY").orElse(null);

        if (billing == null) {

            billing = BillingEntity.builder()
                    .billingId(UUID.randomUUID().toString())
                    .patientId(billingChargeRequestDTO.getPatientId())
                    .receptionId(receptionId)
                    .admissionId(admissionId)
                    .billingStatus("READY")
                    .totalAmount(amount.toString())
                    .insuranceAmount(insuranceAmount.toString())
                    .patientAmount(patientAmount.toString())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            // 이미 있는 billing이면 이번 charge 몫만큼 헤더 합계에 더해준다
            BigDecimal existingTotal = new BigDecimal(billing.getTotalAmount());
            BigDecimal existingInsurance = new BigDecimal(billing.getInsuranceAmount());
            BigDecimal existingPatient = new BigDecimal(billing.getPatientAmount());

            billing.setTotalAmount(existingTotal.add(amount).toString());
            billing.setInsuranceAmount(existingInsurance.add(insuranceAmount).toString());
            billing.setPatientAmount(existingPatient.add(patientAmount).toString());
            billing.setUpdatedAt(LocalDateTime.now());
        }

        // save()만 쓰면 Hibernate가 INSERT/UPDATE를 바로 DB에 안 보내고 영속성 컨텍스트에만 담아둘 수 있어서,
        // 바로 뒤 MyBatis(insertBillingDetail)가 billing_id를 참조할 때 아직 DB에 없어 FK 위반이 남.
        // saveAndFlush로 즉시 반영해서 순서를 보장한다.
        billingRepository.saveAndFlush(billing);

        String billingId = billing.getBillingId();

        billingCharge.setBillingId(billingId);
        billingCharge.setBillingDetailId(UUID.randomUUID().toString());
        billingDetailRepository.insertBillingDetail(billingCharge);
    }

}