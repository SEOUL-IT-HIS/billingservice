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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class BillingChargeServiceImpl implements BillingChargeService {

    private final BillingMasterRepository billingMasterRepository;
    private final BillingDetailRepository billingDetailRepository;
    private final BillingRepository billingRepository;

    @Override
    public void createCharge(BillingChargeRequestDTO billingChargeRequestDTO) {
        String receptionId = billingChargeRequestDTO.getReceptionId();   //접수 id get
        String admissionId = billingChargeRequestDTO.getAdmissionId();   //입원 id get

        if (receptionId == null && admissionId == null) {
            throw new BusinessException(ErrorCode.BILLING_RECEPTION_OR_ADMISSION_ID_REQUIRED);
        } //접수id 입원id 둘다없으면 오류

        // feeCode로 수납기준정보(billing_master)를 조회해 billingMasterId를 확보
        BillingMasterEntity billingMaster = billingMasterRepository.findByFeeCode(billingChargeRequestDTO.getFeeCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND));

        // amount는 호출자가 보낸 값을 믿지 않고 unitPrice(수가 단가) × quantity로 직접 계산한다.
        // (호출자가 amount를 0으로 보내거나 아예 안 보내는 경우가 있어서 그대로 믿으면 안 됨)
        BigDecimal quantity = new BigDecimal(billingChargeRequestDTO.getQuantity());
        BigDecimal amount = billingMaster.getDefaultPrice().multiply(quantity);

        // 보험/본인부담금 분리 - 실제 급여기준표 반영 전 임시 규칙:
        // 비급여(NON_INS)는 전액 본인부담, 급여(그 외)는 본인부담 30% / 보험부담 70%로 고정 계산
        BigDecimal patientAmount;
        BigDecimal insuranceAmount;
        if ("NON_INS".equals(billingMaster.getInsuranceTypeCode())) {
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
                .unitPrice(billingMaster.getDefaultPrice().toString())
                .quantity(billingChargeRequestDTO.getQuantity())
                .amount(amount.toString())
                .billingMasterId(billingMaster.getBillingMasterId())
                .build();

        BillingEntity billing = receptionId != null
                ? billingRepository.findByReceptionId(receptionId).orElse(null)
                : billingRepository.findByAdmissionId(admissionId).orElse(null);

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