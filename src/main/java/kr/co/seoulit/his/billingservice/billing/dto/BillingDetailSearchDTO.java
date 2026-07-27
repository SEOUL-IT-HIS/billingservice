package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BillingDetailSearchDTO {
    //환자이름 조건 검색 
    private String patientName;
}