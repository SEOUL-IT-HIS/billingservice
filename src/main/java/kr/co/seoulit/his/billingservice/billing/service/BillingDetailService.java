package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;

import java.util.List;

public interface BillingDetailService {

    List<BillingSummaryDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);

    List<BillingDetailDTO> getBillingDetailsByBillingId(String billingId);

    void updateBillingStatusToSuccess(String billingId);
    //상태값 ready->success 로 변경
    BillingDetailDTO getBillingStatus(String billingDetailId);
    //status로 조회
    List<BillingDetailDTO> getVisitBillingPreview(String visitId);
    //방문id로 조회
    List<BillingDetailDTO> getAdmissionBillingPreview(String admissionId);
    //입퇴원id로 조회
}
