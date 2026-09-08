package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.entity.PaymentEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.billing.repository.PaymentRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

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
        PaymentEntity payment = PaymentEntity.builder()
                .paymentId(UUID.randomUUID().toString())
                .billingId(header.getBillingId())
                .paymentMethodCode(request.getPaymentMethodCode())
                .paymentAmount(header.getTotalAmount())
                .paymentStatus("APPROVED")
                .paymentAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        paymentRepository.save(payment);
    }
}
