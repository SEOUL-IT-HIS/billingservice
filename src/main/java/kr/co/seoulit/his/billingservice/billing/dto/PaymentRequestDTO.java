package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class PaymentRequestDTO {

    private String billingId;
    private List<String> billingIds;
    //환자의 미수납 건을 한 번에 결제할 때 사용. 값이 있으면 billingId 대신 이 목록 전체를 결제
    private String paymentMethodCode;

    public PaymentRequestDTO(List<String> billingIds, String paymentMethodCode) {
        this(null, billingIds, paymentMethodCode);
    }
}
