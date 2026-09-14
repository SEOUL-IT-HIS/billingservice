package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PaymentRequestDTO {

    private String billingId;
    private String paymentMethodCode;
}
