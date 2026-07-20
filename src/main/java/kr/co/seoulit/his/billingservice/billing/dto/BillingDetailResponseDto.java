package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillingDetailResponseDto {

    private String billingDetailId;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
    private String detailStatus;
    private String feeName;             // BILLING_MASTER JOIN
    private String insuranceTypeCode;   // BILLING_MASTER JOIN
    private LocalDateTime occurredAt;
}

//조회용,join결과값 매핑
