package kr.co.seoulit.his.billingservice.billing.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingDetailSearchDTO{
    private String patientName;

    //patientName으로 환자서비스에서 조회해온 patientId 목록 (서비스 레이어에서 채움)
    private List<String> patientIds;
}
