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
    private String addr;
    private String tel;
    private String birthDate;
    //환자 테이블에서 들고올 정보

    private String billingId;
    private String billingStatus;
    //수납 테이블에서 들고올 정보
}

// 환자검색 -> 중복된 이름 포함해서 조회결과를 List