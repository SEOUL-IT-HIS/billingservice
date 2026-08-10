package kr.co.seoulit.his.billingservice.charge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingChargeResponseDTO {

    private String patientId;
    private String visitId;
    private String admissionId;
    private String sourceServiceCode;
    private String sourceRecordId;
    private String feeCode;
    private String itemName;
    private String quantity;
    private String amount;

    // 타서비스 요청에는 없고, BillingChargeServiceImpl이 저장 직전에 채워서
    // insertBilling/insertBillingDetail 매퍼 파라미터로 재사용하는 내부 식별자
    private String billingId;
    private String billingDetailId;
    private String billingMasterId;

}