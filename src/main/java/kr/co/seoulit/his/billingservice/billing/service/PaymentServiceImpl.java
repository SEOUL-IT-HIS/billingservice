package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.entity.PaymentEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.billing.repository.PaymentRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
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

    @Override
    public void processPayment(PaymentRequestDTO request) {
        List<BillingDetailItemDTO> items =
                billingDetailRepository.findBillingDetailFull(request.getBillingId());

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
                    .paymentMethodCode(request.getPaymentMethodCode())
                    .paymentAmount(header.getTotalAmount())
                    .paymentStatus("APPROVED")
                    .paymentAt(now)
                    .receiptNo(generateReceiptNo(now))
                    .createdAt(now)
                    .updatedAt(now)
                    .build();

            try {
                paymentRepository.saveAndFlush(payment);
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
}
