package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class BillingDetailDTO{
    private String billingDetailId;
    private String billingMasterId;
    private String sourceServiceCode;
    private String sourceRecordId;
    private String feeCode;
    private String itemName;
    private String quantity;
    private String unitPrice;
    private String amount;
    private String detailStatus;
    private String occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}