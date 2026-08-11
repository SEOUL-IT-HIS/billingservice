package kr.co.seoulit.his.billingservice.charge.service;

import kr.co.seoulit.his.billingservice.billing.entity.BillingDetailEntity;
import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailJpaRepository;
import kr.co.seoulit.his.billingservice.billing.repository.BillingRepository;
import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;
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
    private final BillingDetailJpaRepository billingDetailJpaRepository;
    private final BillingRepository billingRepository;

    @Override
    public void createCharge(BillingChargeRequestDTO billingChargeRequestDTO) {
        String visitId = billingChargeRequestDTO.getVisitId();           //방문 id get
        String admissionId = billingChargeRequestDTO.getAdmissionId();   //입원 id get

        if (visitId == null && admissionId == null) {
            throw new BusinessException(ErrorCode.BILLING_VISIT_OR_ADMISSION_ID_REQUIRED);
        } //방문id 입원id 둘다없으면 오류

        // feeCode로 수납기준정보(billing_master)를 조회해 billingMasterId를 확보
        BillingMasterEntity billingMaster = billingMasterRepository.findByFeeCode(billingChargeRequestDTO.getFeeCode())
                .orElseThrow(() -> new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND));

        BillingEntity billing = visitId != null
                ? billingRepository.findByVisitId(visitId).orElse(null)
                : billingRepository.findByAdmissionId(admissionId).orElse(null);

        if (billing == null) {

            billing = BillingEntity.builder()
                    .billingId(UUID.randomUUID().toString())
                    .patientId(billingChargeRequestDTO.getPatientId())
                    .visitId(visitId)
                    .admissionId(admissionId)
                    .billingStatus("READY")
                    .createdAt(LocalDateTime.now())
                    .build();

            billingRepository.save(billing);
        }

        String billingId = billing.getBillingId();

        BillingDetailEntity billingDetail = BillingDetailEntity.builder()
                .billingDetailId(UUID.randomUUID().toString())
                .billingId(billingId)
                .billingMasterId(billingMaster.getBillingMasterId())
                .sourceServiceCode(billingChargeRequestDTO.getSourceServiceCode())
                .sourceRecordId(billingChargeRequestDTO.getSourceRecordId())
                .quantity(Integer.valueOf(billingChargeRequestDTO.getQuantity()))
                .amount(Long.valueOf(billingChargeRequestDTO.getAmount()))
                .detailStatus("READY")
                .createdAt(LocalDateTime.now())
                .build();

        billingDetailJpaRepository.save(billingDetail);
    }
}