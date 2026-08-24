package kr.co.seoulit.his.billingservice.inquiry.history.service;

import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySummaryDTO;

import java.util.List;

public interface BillingHistoryService {

    List<BillingHistoryDTO> getBillinghistoryByPatient(String patientId);

    List<BillingHistorySummaryDTO> searchBillingHistoryByName(BillingHistorySearchDTO searchDTO);
}
