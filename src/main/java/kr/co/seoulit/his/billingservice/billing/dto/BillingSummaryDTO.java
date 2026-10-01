package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingSummaryDTO{

    private String patientId;
    private String patientName;
    private String address;
    private String addressDetail;
    private String phoneNo;
    private String birthDate;
    //환자 테이블에서 들고올 정보

    private String billingId;
    private String billingStatus;
    private String billingType;   // OUTPATIENT / INPATIENT
    private Long totalAmount;     // 결제 대기 항목 합계 (상세조회 화면의 totalAmount와 같은 기준)
    private String createdAt;     // billing 생성일시 (yyyy-MM-dd HH:mm)
    //수납 테이블에서 들고올 정보 - 같은 환자의 여러 건을 목록에서 구분하기 위해 구분/금액/일시를 같이 내려줌
}

// 환자검색 -> 중복된 이름 포함해서 조회결과를 List