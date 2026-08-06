package kr.co.seoulit.his.billingservice.businessdelegate.patient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class PatientBusinessDelegate {

    private final RestTemplate restTemplate;
    private final String patientServiceBaseUrl;

    public PatientBusinessDelegate(
            RestTemplate restTemplate,
            @Value("${patient.service.base-url}") String patientServiceBaseUrl
    ) {
        this.restTemplate = restTemplate;
        this.patientServiceBaseUrl = patientServiceBaseUrl;
    }

    // TODO: 이름 검색 쿼리 파라미터명(patientName) 실제 스펙과 일치하는지 확인 필요
    public List<PatientDTO> searchPatientsByName(String patientName) {
        PatientDTO[] patients = restTemplate.getForObject(
                patientServiceBaseUrl + "/api/patient?patientName={patientName}",
                PatientDTO[].class,
                patientName
        );
        return patients == null ? List.of() : Arrays.asList(patients);
    }

    public PatientDTO getPatientById(String patientId) {
        return restTemplate.getForObject(
                patientServiceBaseUrl + "/api/patient/{patientId}",
                PatientDTO.class,
                patientId
        );
    }

    // TODO: 다건 조회 쿼리 파라미터명(patientIds) 및 콤마 구분 방식이 실제 스펙과 일치하는지 확인 필요
    public List<PatientDTO> getPatientsById(List<String> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return List.of();
        }
        PatientDTO[] patients = restTemplate.getForObject(
                patientServiceBaseUrl + "/api/patient?patientIds={patientIds}",
                PatientDTO[].class,
                String.join(",", patientIds)
        );
        return patients == null ? List.of() : Arrays.asList(patients);
    }
}
