package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;

import java.util.List;

public interface BillingDetailService {

    List<BillingSummaryDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);
    //환자 검색- 환자서비스에 REST api 호출하고 결과값 반환

    BillingDetailResponseDTO getBillingDetails(String billingId);
    //상세보기 버튼 클릭 후 billingId 기준 상세 항목 조회
        
    void updateBillingStatusToSuccess(String billingId);
    //상태값 ready->success 로 변경
    BillingDetailDTO getBillingStatus(String billingDetailId);
    //status로 조회
    List<BillingDetailDTO> getVisitBillingPreview(String visitId);
    //방문id로 조회
    List<BillingDetailDTO> getAdmissionBillingPreview(String admissionId);
    //입퇴원id로 조회
}
