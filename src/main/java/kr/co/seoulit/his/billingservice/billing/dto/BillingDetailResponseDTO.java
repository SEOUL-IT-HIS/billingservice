package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BillingDetailResponseDTO {

    private String billingId;

    // 환자 정보
    private String patientId;
    private String patientName;
    private String tel;
    private String addr;

    // 진료 구분용 ID
    private String visitId;
    private String admissionId;

    // 진료비 합계
    private Long outpatientAmount;
    private Long inpatientAmount;
    private Long totalAmount;

    private String billingStatus;
}