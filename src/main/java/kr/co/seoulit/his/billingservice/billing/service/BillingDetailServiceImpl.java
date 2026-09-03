package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingStatusDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDTO;
import kr.co.seoulit.his.billingservice.businessdelegate.patient.PatientBusinessDelegate;
import kr.co.seoulit.his.billingservice.businessdelegate.patient.PatientDTO;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class BillingDetailServiceImpl implements BillingDetailService {

    private final BillingDetailRepository billingDetailRepository;
    private final PatientBusinessDelegate patientBusinessDelegate;

    @Override
    public List<BillingSummaryDTO> searchBillingDetails(
            BillingDetailSearchDTO searchDTO) {
        String patientName = searchDTO.getPatientName();

        if (patientName != null && !patientName.isBlank()) {
            List<PatientDTO> patients = patientBusinessDelegate.searchPatientsByName(patientName);
            if (patients.isEmpty()) {
                return List.of();}
            searchDTO.setPatientIds(
                             patients.stream()
                            .map(PatientDTO::getPatientId)
                            .toList());}

        List<BillingSummaryDTO> summaries = billingDetailRepository.searchBillingDetails(searchDTO);
        if (summaries.isEmpty()) { return summaries; }

        //검색 결과에 표시할 환자 이름/전화번호/주소를 환자서비스에서 일괄 조회해 채움
        List<String> patientIds = summaries.stream()
                .map(BillingSummaryDTO::getPatientId)
                .distinct()
                .toList();
        Map<String, PatientDTO> patientById = patientBusinessDelegate.getPatientsById(patientIds).stream()
                .collect(Collectors.toMap(PatientDTO::getPatientId, Function.identity()));

        summaries.forEach(summary -> {
            PatientDTO patient = patientById.get(summary.getPatientId());
            if (patient != null) {
                summary.setPatientName(patient.getPatientName());
                summary.setPhoneNo(patient.getPhoneNo());
                summary.setAddress(patient.getAddress());
                summary.setAddressDetail(patient.getAddressDetail());
                summary.setBirthDate(patient.getBirthDate());}
        });

        return summaries;
    }// 환자 이름으로 검색해서 청구 상세 내역 조회
    
    
    /**
     * 상세보기 버튼 클릭 후 billingId 기준 헤더 + 상세 항목(items) 통합 조회
     */
    @Override
    public BillingDetailResponseDTO getBillingDetails(String billingId) {

        // billingId에 해당하는 billing_detail 행 전체 조회 (헤더 값은 윈도우 함수로 매 행에 반복돼 있음)
        List<BillingDetailItemDTO> items = billingDetailRepository.findBillingDetailFull(billingId);

        // 해당 수납 건이 존재하지 않으면 예외 처리
        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }

        // 모든 행이 같은 헤더 값을 갖고 있으므로 첫 행에서 헤더 정보를 꺼냄
        BillingDetailItemDTO header = items.get(0);

        // 수납 데이터의 patientId로 환자 서비스 조회
        PatientDTO patient =
                patientBusinessDelegate.getPatientById(header.getPatientId());

        // 환자 정보가 존재하지 않으면 예외 처리
        if (patient == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }

        return BillingDetailResponseDTO.builder()
                .billingId(header.getBillingId())
                .receptionId(header.getReceptionId())
                .admissionId(header.getAdmissionId())
                .billingStatus(header.getBillingStatus())
                .totalAmount(header.getTotalAmount())
                .outpatientAmount(header.getReceptionId() != null ? header.getTotalAmount() : 0L)
                .inpatientAmount(header.getAdmissionId() != null ? header.getTotalAmount() : 0L)
                .patientId(patient.getPatientId())
                .patientName(patient.getPatientName())
                .phoneNo(patient.getPhoneNo())
                .address(patient.getAddress())
                .addressDetail(patient.getAddressDetail())
                .birthDate(patient.getBirthDate())
                .items(items)
                .build();
    }
    //메인 페이지에 불러와야할 데이터들. 환자 한명의 진료비 상세 조회
    //외래비 + 입원비 = 총합

   

    //상태값 변경
    @Override
    public void updateBillingStatusToSuccess(String billingId) {

        BillingStatusDTO billingStatusDTO =
                billingDetailRepository.selectBillingDetailForStatusUpdate(billingId);

        if (billingStatusDTO == null) {
            throw new BusinessException(
                    ErrorCode.BILLING_MASTER_NOT_BILLINGID);
        }

        billingDetailRepository.updateBillingStatusToSuccess(billingId);
    }

}