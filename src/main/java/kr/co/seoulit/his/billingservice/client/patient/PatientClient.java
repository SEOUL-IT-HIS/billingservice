package kr.co.seoulit.his.billingservice.client.patient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PatientClient {

    private final RestTemplate restTemplate;
    private final String patientServiceBaseUrl;

    public PatientClient(
            RestTemplate restTemplate,
            @Value("${patient.service.base-url}") String patientServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.patientServiceBaseUrl = patientServiceBaseUrl;
    }

    // TODO: 환자서비스의 실제 이름 검색 API 경로/파라미터명 확인 후 조정 필요
    public List<PatientDTO> searchPatientsByName(String patientName) {
        PatientDTO[] patients = restTemplate.getForObject(
                patientServiceBaseUrl + "/api/patients?patientName={patientName}",
                PatientDTO[].class,
                patientName
        );
        return patients == null ? List.of() : Arrays.asList(patients);
    }
}
