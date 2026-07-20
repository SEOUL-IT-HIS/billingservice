package kr.co.seoulit.his.billingservice.master.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicalDetailDto {

    private String billingMasterId;
    private String sourceServiceCode;
    private String feeCode;
    private String feeName;
    private BigDecimal defaultPrice;
    private String categoryCode;
    private String insuranceTypeCode;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private String useYn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
