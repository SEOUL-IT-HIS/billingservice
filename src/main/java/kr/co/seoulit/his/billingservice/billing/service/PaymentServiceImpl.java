package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.PayCancelIdDTO;
import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.entity.PaymentEntity;
import kr.co.seoulit.his.billingservice.billing.event.SettlementCompletedEvent;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.billing.repository.PaymentRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    // 영수증번호 형식: YYMM-XXXXX (10자, 뒷자리는 0~99999 랜덤)
    private static final DateTimeFormatter RECEIPT_NO_PREFIX_FORMAT = DateTimeFormatter.ofPattern("yyMM");
    private static final int RECEIPT_NO_MAX_ATTEMPTS = 5;

    private final BillingDetailRepository billingDetailRepository;
    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;

    // 여러 건을 한 번에 결제해도 payment 행은 billing마다 하나씩 남긴다 (수납이력/취소가 billingId 단위라서).
    // 클래스 전체가 @Transactional이라 중간에 한 건이라도 실패하면 전부 롤백됨.
    @Override
    public void processPayment(PaymentRequestDTO request) {
        List<String> billingIds = request.getBillingIds() != null && !request.getBillingIds().isEmpty()
                ? request.getBillingIds()
                : (request.getBillingId() != null ? List.of(request.getBillingId()) : List.of());

        if (billingIds.isEmpty()) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }

        for (String billingId : billingIds) {
            payBilling(billingId, request.getPaymentMethodCode());
        }
    }

    private void payBilling(String billingId, String paymentMethodCode) {
        List<BillingDetailItemDTO> items =
                billingDetailRepository.findBillingDetailFull(billingId);

        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }

        BillingDetailItemDTO header = items.get(0);

        int updated = billingDetailRepository.updateBillingStatusToSuccess(header.getBillingId());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BILLING_ALREADY_PROCESSED);
        }

        LocalDateTime now = LocalDateTime.now();

        // RECEIPT_NO는 UNIQUE 제약이 걸려있으므로, 랜덤 생성값이 겹치면 새로 뽑아서 재시도한다.
        for (int attempt = 1; attempt <= RECEIPT_NO_MAX_ATTEMPTS; attempt++) {
            PaymentEntity payment = PaymentEntity.builder()
                    .paymentId(UUID.randomUUID().toString())
                    .billingId(header.getBillingId())
                    .paymentMethodCode(paymentMethodCode)
                    .paymentAmount(header.getTotalAmount())
                    .paymentStatus("APPROVED")
                    .paymentAt(now)
                    .receiptNo(generateReceiptNo(now))
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            try {
                paymentRepository.saveAndFlush(payment);

                // 입원 건이면 병동에 퇴원정산 완료를 알린다 (외래 건은 admissionId가 없어서 대상 아님).
                // 실제 카프카 발행은 커밋 후 SettlementCompletedKafkaPublisher에서 한다.
                if (header.getAdmissionId() != null) {
                    eventPublisher.publishEvent(new SettlementCompletedEvent(header.getAdmissionId()));
                }
                return;
            } catch (DataIntegrityViolationException e) {
                if (attempt == RECEIPT_NO_MAX_ATTEMPTS) {
                    throw new BusinessException(ErrorCode.PAYMENT_RECEIPT_NO_GENERATION_FAILED);
                }
            }
        }
    }

    private String generateReceiptNo(LocalDateTime now) {
        String prefix = now.format(RECEIPT_NO_PREFIX_FORMAT);
        int sequence = ThreadLocalRandom.current().nextInt(100_000);
        return prefix + "-" + String.format("%05d", sequence);
    }

    @Override
    public PayCancelIdDTO paymentCancel(PayCancelIdDTO request) {
        String patientId = request.getPatientId();

        if (patientId == null) {
            throw new BusinessException(
                    ErrorCode.PATIENT_NOT_FOUND);
        }

        return request;
    }
    // 결제 취소 처리 (카드, 현금 등 결제 수단에 따라 실제 취소 로직은 다를 수 있음)

}
