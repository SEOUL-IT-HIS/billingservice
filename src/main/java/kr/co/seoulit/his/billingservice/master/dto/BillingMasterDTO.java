package kr.co.seoulit.his.billingservice.master.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingMasterDTO{
       private String billingMasterId;
       private String sourceServiceCode;
       private String feeCode;
       private String feeName;
       private String defaultPrice;
       private String categoryCode;
       private String insuranceTypeCode;
       private String effectiveFrom;
       private String effectiveTo;
       private String useYn;
       private LocalDateTime createdAt;
       private LocalDateTime updatedAt;
}

// <--DB에선 billing_master_id -> billingMasterId 스네이크에서 카멜식으로 변경 하는 과정-->
// <--entity.java에서 변환-->