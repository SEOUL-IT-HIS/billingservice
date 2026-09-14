package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingStatusDTO {

    private String billingDetailId;
    private String billingId;

    private String detailStatus;
    private String billingStatus;
}
//수납 가능 여부 및 처리 상태 확인
