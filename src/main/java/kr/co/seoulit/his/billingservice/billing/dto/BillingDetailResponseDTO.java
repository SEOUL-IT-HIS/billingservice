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

    private List<String> billingIds;
    //이번 상세조회에 포함된 billingId 목록 - 환자 단위 조회면 미수납 건 전부, 결제 요청 시 그대로 보냄

    private Long totalAmount;
    //billing_detail 합산 금액 (billingId 단건이라 외래/입원 구분 없이 하나로 충분함)

    private Long outpatientAmount;
    private Long inpatientAmount;
    //billingId 단건 조회면 둘 중 하나는 totalAmount와 같고 나머지는 0
    //환자 단위 조회면 외래 건 합계 / 입원 건 합계 - 프론트엔드 외래/입원 구분 표시용

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