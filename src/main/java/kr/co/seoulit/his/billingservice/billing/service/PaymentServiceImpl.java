package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDTO;
import kr.co.seoulit.his.billingservice.billing.entity.PaymentEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.billing.repository.PaymentRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BillingDetailRepository billingDetailRepository;
    private final PaymentRepository paymentRepository;

    @Override
    public void processPayment(PaymentRequestDTO request) {
        BillingDetailResponseDTO billing =
                billingDetailRepository.findBillingSummaryByBillingId(request.getBillingId());

        if (billing == null) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }

        int updated = billingDetailRepository.updateBillingStatusToSuccess(billing.getBillingId());
        if (updated == 0) {
            throw new BusinessException(ErrorCode.BILLING_ALREADY_PROCESSED);
        }

        PaymentEntity payment = PaymentEntity.builder()
                .paymentId(UUID.randomUUID().toString())
                .billingId(billing.getBillingId())
                .paymentMethodCode(request.getPaymentMethodCode())
                .paymentAmount(billing.getTotalAmount())
                .paymentStatus("SUCCESS")
                .paymentAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
    }
}
