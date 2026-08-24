package kr.co.seoulit.his.billingservice.inquiry.history.repository;

import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySummaryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BillingHistoryRepository {

    List<BillingHistorySummaryDTO> searchBillingHistory(BillingHistorySearchDTO searchDTO);

    List<BillingHistoryDTO> findBillingHistoryByPatientId(String patientId);
    //xml(sql)과 매핑 해주는 파일 및 메서드.
}