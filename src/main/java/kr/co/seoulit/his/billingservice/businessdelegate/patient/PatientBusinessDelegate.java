package kr.co.seoulit.his.billingservice.businessdelegate.patient;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Slf4j
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
        try {
            PatientDTO[] patients = restTemplate.getForObject(
                    patientServiceBaseUrl + "/api/patient?patientName={patientName}",
                    PatientDTO[].class,
                    patientName
            );
            return patients == null ? List.of() : Arrays.asList(patients);
        } catch (RestClientException e) {
            log.error("환자 서비스 이름 검색 실패 (patientName={}): {}", patientName, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PATIENT_SERVICE_UNAVAILABLE);
        }
    }

    // 환자서비스는 {code, message, data} 봉투로 응답하므로 ApiResponse<PatientDTO>로 언래핑 후 data만 꺼낸다
    public PatientDTO getPatientById(String patientId) {
        try {
            ResponseEntity<ApiResponse<PatientDTO>> response = restTemplate.exchange(
                    patientServiceBaseUrl + "/api/patient/{patientId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<PatientDTO>>() {},
                    patientId
            );
            ApiResponse<PatientDTO> body = response.getBody();
            return body == null ? null : body.getData();
        } catch (RestClientException e) {
            log.error("환자 서비스 단건 조회 실패 (patientId={}): {}", patientId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PATIENT_SERVICE_UNAVAILABLE);
        }
    }

    // TODO: 다건 조회 쿼리 파라미터명(patientIds) 및 콤마 구분 방식이 실제 스펙과 일치하는지 확인 필요
    public List<PatientDTO> getPatientsById(List<String> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return List.of();
        }
        try {
            PatientDTO[] patients = restTemplate.getForObject(
                    patientServiceBaseUrl + "/api/patient?patientIds={patientIds}",
                    PatientDTO[].class,
                    String.join(",", patientIds)
            );
            return patients == null ? List.of() : Arrays.asList(patients);
        } catch (RestClientException e) {
            log.error("환자 서비스 다건 조회 실패 (patientIds={}): {}", patientIds, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PATIENT_SERVICE_UNAVAILABLE);
        }
    }
}
