package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingSummaryDTO {

    private String patientId;
    private String patientName;

    private String visitId;
    private String admissionId;

    private Long outpatientAmount;
    private Long inpatientAmount;

    private Long totalAmount;
    private Long insuranceAmount;
    private Long patientAmount;

    private String billingStatus;
}