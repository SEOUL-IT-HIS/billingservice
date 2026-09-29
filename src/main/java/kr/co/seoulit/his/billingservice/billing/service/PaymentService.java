package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.PayCancelIdDTO;
import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;

public interface PaymentService {

    void processPayment(PaymentRequestDTO request);

    PayCancelIdDTO paymentCancel(PayCancelIdDTO request);

}
