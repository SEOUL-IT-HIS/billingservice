package kr.co.seoulit.his.billingservice.client.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientDTO {

    private String patientId;
    private String patientName;
    private String tel;
    private String addr;
}