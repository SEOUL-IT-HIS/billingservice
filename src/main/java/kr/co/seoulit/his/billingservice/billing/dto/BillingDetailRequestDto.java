package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillingDetailRequestDto {

    private String billingMasterId;    // 참조할 수납기준정보 FK
    private String sourceServiceCode;
    private String sourceRecordId;
    private String feeCode;
    private String itemName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal amount;
}