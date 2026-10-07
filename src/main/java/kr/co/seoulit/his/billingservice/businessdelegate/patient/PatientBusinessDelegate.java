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

import java.util.ArrayList;
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

    // 환자서비스는 {code, message, data} 봉투로 응답하므로 ApiResponse<List<PatientDTO>>로 언래핑 후 data만 꺼낸다
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
            PatientDTO patient = body == null ? null : body.getData();
            if (patient != null) {
                fillContact(patient);
            }
            return patient;
        } catch (RestClientException e) {
            log.error("환자 서비스 단건 조회 실패 (patientId={}): {}", patientId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.PATIENT_SERVICE_UNAVAILABLE);
        }
    }

    // 환자 본문 응답에는 전화번호/주소가 없고 연락처 API에 따로 있어서, 대표(없으면 첫 번째) 활성 연락처를 채운다.
    // 연락처는 부가정보라 조회에 실패해도 수납 흐름이 막히지 않도록 경고 로그만 남기고 비워둔다.
    private void fillContact(PatientDTO patient) {
        try {
            ResponseEntity<ApiResponse<List<PatientContactDTO>>> response = restTemplate.exchange(
                    patientServiceBaseUrl + "/api/patient/{patientId}/contacts",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<List<PatientContactDTO>>>() {},
                    patient.getPatientId()
            );
            ApiResponse<List<PatientContactDTO>> body = response.getBody();
            List<PatientContactDTO> contacts = body == null || body.getData() == null ? List.of() : body.getData();

            List<PatientContactDTO> active = contacts.stream()
                    .filter(contact -> !"N".equals(contact.getActiveYn()))
                    .toList();
            active.stream()
                    .filter(contact -> "Y".equals(contact.getPrimaryYn()))
                    .findFirst()
                    .or(() -> active.stream().findFirst())
                    .ifPresent(contact -> {
                        patient.setPhoneNo(contact.getPhoneNo());
                        patient.setAddress(contact.getAddress());
                        patient.setAddressDetail(contact.getAddressDetail());
                    });
        } catch (RestClientException e) {
            log.warn("환자 연락처 조회 실패 (patientId={}): {}", patient.getPatientId(), e.getMessage());
        }
    }

    // 환자서비스에 다건 조회 API가 없어(단건 조회만 존재), id별로 단건 조회를 반복 호출해 모은다
    public List<PatientDTO> getPatientsById(List<String> patientIds) {
        if (patientIds == null || patientIds.isEmpty()) {
            return List.of();
        }
        // 목록 화면용이라 환자 한 명이 조회 실패(없는 ID 등)해도 나머지는 보여주기 위해 건별 실패는 건너뜀.
        // 전부 실패하면 환자 서비스 장애로 보고 예외를 그대로 던짐.
        List<PatientDTO> patients = new ArrayList<>();
        BusinessException lastFailure = null;
        for (String patientId : patientIds) {
            try {
                PatientDTO patient = getPatientById(patientId);
                if (patient != null) {
                    patients.add(patient);
                }
            } catch (BusinessException e) {
                log.warn("환자 정보 조회 실패로 목록에서 이름 없이 표시 (patientId={})", patientId);
                lastFailure = e;
            }
        }
        if (patients.isEmpty() && lastFailure != null) {
            throw lastFailure;
        }
        return patients;
    }
}
