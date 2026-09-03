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

        // 요청 DTO(타서비스 입력)를 내부 저장용 DTO로 변환하면서 billingMasterId를 채움
        BillingChargeResponseDTO billingCharge = BillingChargeResponseDTO.builder()
                .receptionId(receptionId)
                .admissionId(admissionId)
                .sourceServiceCode(billingChargeRequestDTO.getSourceServiceCode())
                .sourceRecordId(billingChargeRequestDTO.getSourceRecordId())
                .feeCode(billingChargeRequestDTO.getFeeCode())
                .itemName(billingChargeRequestDTO.getItemName())
                .unitPrice(billingMaster.getDefaultPrice().toString())
                .quantity(billingChargeRequestDTO.getQuantity())
                .amount(billingChargeRequestDTO.getAmount())
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
                    // 보험/본인부담금 분리 계산 로직이 아직 없어서 일단 0으로 채움 (컬럼이 NOT NULL이라 비워두면 INSERT 실패함)
                    .totalAmount("0")
                    .insuranceAmount("0")
                    .patientAmount("0")
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            billingRepository.save(billing);
        }

        String billingId = billing.getBillingId();

        billingCharge.setBillingId(billingId);
        billingCharge.setBillingDetailId(UUID.randomUUID().toString());
        billingDetailRepository.insertBillingDetail(billingCharge);
    }

}