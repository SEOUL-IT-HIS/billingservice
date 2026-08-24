package kr.co.seoulit.his.billingservice.inquiry.history.service;

import kr.co.seoulit.his.billingservice.businessdelegate.patient.PatientBusinessDelegate;
import kr.co.seoulit.his.billingservice.businessdelegate.patient.PatientDTO;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySummaryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.repository.BillingHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor

public class BillingHistoryServiceImpl implements BillingHistoryService {

    private final BillingHistoryRepository billingHistoryRepository;
    private final PatientBusinessDelegate patientBusinessDelegate;

    //환자 이름을 검색해서 리스트로 반환
    @Override
    public List<BillingHistorySummaryDTO> searchBillingHistoryByName(BillingHistorySearchDTO searchDTO) {
        String patientName = searchDTO.getPatientName();
        if (patientName != null && !patientName.isBlank()) {
            List<PatientDTO> patients = patientBusinessDelegate.searchPatientsByName(patientName);
            if (patients.isEmpty()) {
                return List.of();
            }

            searchDTO.setPatientIds(
                    patients.stream().map(PatientDTO::getPatientId).toList());
        }

        List<BillingHistorySummaryDTO> summaries = billingHistoryRepository.searchBillingHistory(searchDTO);
        if (summaries.isEmpty()) {
            return summaries;
        }
        //리스트로 하기전에 결제 상태가 완료된 것만 Query로 추출해서 summaries에 담는다.

        List<String> patientIds = summaries
                .stream()
                .map(BillingHistorySummaryDTO::getPatientId)
                .distinct()
                .toList();
        Map<String, PatientDTO> patientById = patientBusinessDelegate.getPatientsById(patientIds)
                .stream().collect(Collectors.toMap(PatientDTO::getPatientId, Function.identity())
                );
        summaries.forEach(summary -> {
            PatientDTO patient = patientById.get(summary.getPatientId());
            if (patient != null) {
                summary.setPatientName(patient.getPatientName());
                summary.setTel(patient.getTel());
                summary.setAddr(patient.getAddr());
            }
        });

        return summaries;
    }

    @Override
    public List<BillingHistoryDTO> getBillinghistoryByPatient(String patientId) {

        List<BillingHistoryDTO> histories = billingHistoryRepository.findBillingHistoryByPatientId(patientId);

        if (histories == null) { return List.of(); }

        PatientDTO patient = patientBusinessDelegate.getPatientById(patientId);

        if (patient == null) { throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND); }

        histories.forEach(history -> history.setPatientName(patient.getPatientName()));

        return histories;
    }
}
