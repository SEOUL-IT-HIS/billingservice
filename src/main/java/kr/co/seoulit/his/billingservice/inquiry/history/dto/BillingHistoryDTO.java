package kr.co.seoulit.his.billingservice.inquiry.history.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingHistoryDTO {

    private String billingId;

    private String patientId;
    private String patientName;
    private String billingType;

    private String paymentId;
    private Integer paymentAmount;
    private String paymentMethod;
    private LocalDateTime paymentAt;
    private String receiptNo;

    private String billingStatus;

}
