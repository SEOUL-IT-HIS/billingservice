package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data

public class BillingDetailDTO {

    private String billingDetailId;
    private String billingMasterId;
    private String billingId;

    private String sourceServiceCode;
    private String sourceRecordId;

    private String feeCode;
    private String itemName;

    private Integer quantity;
    private Long unitPrice;
    private Long amount;

    private String detailStatus;

    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}