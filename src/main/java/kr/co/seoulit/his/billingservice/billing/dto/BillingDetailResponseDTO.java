package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingDetailResponseDTO{

    private String billingId;
    private String receptionId;
    private String admissionId;
    private String billingStatus;
    //수납 테이블 정보

    private Long totalAmount;
    //billing_detail 합산 금액 (billingId 단건이라 외래/입원 구분 없이 하나로 충분함)

    private Long outpatientAmount;
    private Long inpatientAmount;
    //billingId 하나는 receptionId 또는 admissionId 둘 중 하나만 가지므로
    //둘 중 하나는 totalAmount와 같고 나머지는 0 - 프론트엔드 외래/입원 구분 표시용

    private String patientId;
    private String patientName;
    private String address;
    private String addressDetail;
    private String phoneNo;
    private String birthDate;
    //환자 서비스 정보 - SQL 아닌 patientBusinessDelegate REST 호출로 채움

    private List<BillingDetailItemDTO> items;
    //검사/진료/약제 등 billing_detail 행 목록
    //환자 진료비 상세조회(메인)
}