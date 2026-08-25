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

    public List<PatientDTO> searchPatientsByName(String patientName) {
        try {
            ResponseEntity<ApiResponse<List<PatientDTO>>> response = restTemplate.exchange(
                    patientServiceBaseUrl + "/api/patient/list?patientName={patientName}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<List<PatientDTO>>>() {},
                    patientName
            );
            ApiResponse<List<PatientDTO>> body = response.getBody();
            return body == null || body.getData() == null ? List.of() : body.getData();
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

    // TODO: patient-service의 /api/patient/list 가 patientIds 파라미터로 필터링을 지원하지 않아
    //  현재는 전체 환자 목록을 받아온다. patient-service 쪽에 patientIds 필터 지원을 요청해야 함.
    public List<PatientDTO> getPatientsById(List<String> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return List.of();
        }
        try {
            ResponseEntity<ApiResponse<List<PatientDTO>>> response = restTemplate.exchange(
                    patientServiceBaseUrl + "/api/patient/list?patientIds={patientIds}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<List<PatientDTO>>>() {},
                    String.join(",", patientIds)
            );
            ApiResponse<List<PatientDTO>> body = response.getBody();
            return body == null || body.getData() == null ? List.of() : body.getData();
        } catch (RestClientException e) {
            log.error("환자 서비스 다건 조회 실패 (patientIds={}): {}", patientIds, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PATIENT_SERVICE_UNAVAILABLE);
        }
    }
}
