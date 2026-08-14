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

    private Integer totalAmount;

    private String billingStatus;
    private LocalDateTime billingAt;

}
