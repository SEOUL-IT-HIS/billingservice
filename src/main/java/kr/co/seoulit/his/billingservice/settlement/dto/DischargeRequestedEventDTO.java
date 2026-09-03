package kr.co.seoulit.his.billingservice.settlement.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class DischargeRequestedEventDTO {

    private String admissionId;
    private String patientId;
    private String admissionDeptId;
    private String requestedAt;
}
