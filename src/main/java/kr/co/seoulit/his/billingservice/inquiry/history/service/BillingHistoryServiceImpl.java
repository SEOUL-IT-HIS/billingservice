package kr.co.seoulit.his.billingservice.inquiry.history.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.businessdelegate.patient.PatientBusinessDelegate;
import kr.co.seoulit.his.billingservice.businessdelegate.patient.PatientDTO;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.repository.BillingHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class BillingHistoryServiceImpl implements BillingHistoryService{

    private final BillingHistoryRepository billingHistoryRepository;
    private final PatientBusinessDelegate patientBusinessDelegate;

    @Override
    public List<BillingHistoryDTO> getBillinghistoryByPatient(String patientId){
             List<BillingHistoryDTO> histories=
                     billingHistoryRepository.findBillingHistoryByPatientId(patientId);
             // Billing+Payment 테이블에서 환자의 수납 이력 조회
             if(histories.isEmpty()){return List.of();}
             return histories;
    }

    @Override
    public List<BillingHistoryDTO> getBillingHistory(
            BillingHistorySearchDTO searchDTO
    ) {
        String patientName= searchDTO.getPatientName();
        if(patientName.isEmpty()){return List.of();}

        List<PatientDTO> patients=patientClient.searchPatientsByName(patientName);
        searchDTO.setPatientIds(
                         patients.stream()
                        .map(PatientDTO::getPatientId)
                        .toList()
        );// 환자 이름으로 환자 조회 후 환자 ID 리스트로 변환

        List<BillingHistoryDTO> histories=billingHistoryRepository.findBillingHistory(searchDTO);
        // Billing+Payment 테이블에서 환자의 수납 이력 조회

        Map<String, PatientDTO> patientById=
                patientBusinessDelegate
                        .getPatientById(patientIds)
                        .stream()
                        .collect(Collectors
                                .toMap(PatientDTO::getPatientId, Function.identity())));
        summaries.forEach(summary -> {
            PatientDTO patient = patientById.get(summary.getPatientId());
            if (patient != null) {
                summary.setPatientName(patient.getPatientName());
                summary.setTel(patient.getTel());
                summary.setAddr(patient.getAddr());}
        });

        return summaries;

        

    }
}
