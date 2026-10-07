package kr.co.seoulit.his.billingservice.businessdelegate.patient;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PatientContactDTO {

    private String address;
    private String addressDetail;
    private String phoneNo;
    private String primaryYn; //대표 주소·연락처 여부 (Y/N)
    private String activeYn;  //활성 여부 (Y/N)
    //환자 서비스 /api/patient/{patientId}/contacts 응답 - 환자 본문에는 연락처가 없어 따로 조회해서 채움
}
