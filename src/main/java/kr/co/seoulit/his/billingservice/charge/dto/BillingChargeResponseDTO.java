package kr.co.seoulit.his.billingservice.charge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingChargeResponseDTO {

    private String billingId;
    private String patientId;
    private String visitId;
    private String admissionId;

    private String billingDetailId;
    private String billingMasterId;
    private String sourceServiceCode;
    private String sourceRecordId;

    private String feeCode;
    private String itemName;

    private String quantity;
    private String amount;
}
