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

public class BillingDetailDTO {

    private String billingDetailId;
    private String billingMasterId;
    private String billingId;

    // 환자별 READY 조회
    private String patientId;
    private String visitId;
    private String admissionId;
    //방문,입원 id로 상세 조회를 위한 칼럼

    private String sourceServiceCode;
    private String sourceRecordId;
    private String feeCode;
    private String itemName;
    private String quantity;
    private String unitPrice;
    private String amount;
    private String detailStatus;
    private LocalDateTime occurredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}