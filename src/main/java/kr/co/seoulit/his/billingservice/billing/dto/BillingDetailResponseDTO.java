package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingDetailResponseDTO{

    private String billingId;
    private String visitId;
    private String admissionId;
    private String billingStatus;
    //수납 테이블 정보

    private Long outpatientAmount;
    private Long inpatientAmount;
    private Long totalAmount;
    //수납 진료/입퇴원 계산으로 만들어진 필드값

    private String patientId;
    private String patientName;
    private String tel;
    private String addr;
    //환자 서비스 정보 - SQL 아닌 patientBusinessDelegate REST 호출로 채움
    //환자 진료비 상세조회(메인)
}