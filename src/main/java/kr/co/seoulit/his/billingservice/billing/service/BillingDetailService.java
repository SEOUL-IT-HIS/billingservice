package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;

import java.util.List;

public interface BillingDetailService {

    List<BillingDetailDTO> getAllBillingDetails();

    BillingDetailDTO getBillingDetailById(String billingDetailId);

    BillingDetailDTO createBillingDetail(BillingDetailDTO billingDetailDTO);

}
