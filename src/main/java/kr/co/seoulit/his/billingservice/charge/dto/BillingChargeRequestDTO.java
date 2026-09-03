package kr.co.seoulit.his.billingservice.charge.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class BillingChargeRequestDTO {

    private String patientId;
    private String receptionId;
    private String admissionId;

    private String sourceServiceCode;
    private String sourceRecordId;

    private String feeCode;
    private String itemName;

    private String quantity;
    private String amount;
}