package kr.co.seoulit.his.billingservice.inquiry.history.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;

import java.util.List;

public interface BillingHistoryService {

    List<BillingHistoryDTO> getBillinghistoryByPatient(String patientId);

    List<BillingHistoryDTO> getBillingHistory(BillingHistorySearchDTO searchDTO);
}
