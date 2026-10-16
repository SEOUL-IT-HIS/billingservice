package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.PayCancelIdDTO;
import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;

public interface PaymentService {

    void processPayment(PaymentRequestDTO request);

    // TODO: 수납취소(현금/카드) 미구현이라 주석처리
//    PayCancelIdDTO paymentCancel(PayCancelIdDTO request);

}
