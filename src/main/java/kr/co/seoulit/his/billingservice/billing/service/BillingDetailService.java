package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;

import java.util.List;

public interface BillingDetailService {

    List<BillingDetailDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);

    BillingDetailDTO getBillingDetailById(String billingDetailId);

    void updateBillingStatusToSuccess(String billingId);

    BillingDetailDTO getBillingStatus(String billingDetailId);
}
