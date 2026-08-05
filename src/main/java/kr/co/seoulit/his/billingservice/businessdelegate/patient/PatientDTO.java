package kr.co.seoulit.his.billingservice.businessdelegate.patient;

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
    //환자 서비스 정보 - SQL 아닌 patientBusinessDelegate REST 호출로 채움
}
