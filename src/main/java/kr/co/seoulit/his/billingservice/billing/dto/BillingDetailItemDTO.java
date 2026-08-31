package kr.co.seoulit.his.billingservice.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingDetailItemDTO{
    private String billingId;
    @JsonIgnore
    private String patientId; //환자서비스에서 patientName 조회하기 위한 내부용 값 - 응답에는 노출하지 않음
    private String patientName;
    private String visitId;
    private String admissionId;
    private String billingStatus;

    private String quantity;
    private String unitPrice;
    private String amount;
    private String detailStatus;
    private String occurredAt;

    private String feeCode;
    private String itemName;

}
// 환자의 진료비 상세조회 에서 진료(외래)별/입원별 상세조회