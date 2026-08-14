package kr.co.seoulit.his.billingservice.inquiry.history.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingHistorySearchDTO {

    private String patientName;

    // 환자 이름으로 환자서비스에서 조회한 patientId 목록
    private List<String> patientIds;
}