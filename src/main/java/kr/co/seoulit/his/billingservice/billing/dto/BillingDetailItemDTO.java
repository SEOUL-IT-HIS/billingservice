package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingDetailItemDTO{
    private String billingId;
    private String patientId;
    private String visitId;
    private String admissionId;
    private String billingStatus;

    private String billingDetailId;
    private String billingMasterId;
    private String sourceServiceCode;
    private String sourceRecordId;
    private String quantity;
    private String unitPrice;
    private String amount;
    private String detailStatus;
    private String occurredAt;
    private String createdAt;
    private String updatedAt;

    private String feeCode;
    private String itemName;

}
// 환자의 진료비 상세조회 에서 진료(외래)별/입원별 상세조회