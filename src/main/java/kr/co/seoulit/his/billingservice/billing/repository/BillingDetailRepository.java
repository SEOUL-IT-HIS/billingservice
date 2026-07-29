package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    List<BillingSummaryDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);
    //수납 담당자가 환자 이름을 검색 + 검색 조건에 맞는 결과값으로 리스트로 반환
    List<BillingDetailDTO> findBillingDetailsByBillingId(String billingId);
    //검색 결과 리스트에서 해당에 맞는 환자 상세조회.

    // 상태별 조회
    List<BillingDetailDTO> findByDetailStatus(String detailStatus);

    // 수납 가능 여부 및 처리 상태 확인
    BillingDetailDTO findBillingStatusByDetailId(String billingDetailId);

    // 상태 변경 전 수납정보 존재 여부 확인
    BillingDetailDTO selectBillingDetailForStatusUpdate(@PathVariable("billingId") String billingId);

    // READY 상태를 SUCCESS로 변경
    void updateBillingStatusToSuccess(@PathVariable("billingId") String billingId);

    //↓방문/입원 id로 상세조회 SQL 쿼리 부르는 메서드
    List<BillingDetailDTO> findBillingPreviewByVisitId(@PathVariable ("visitId") String visitId);
    List<BillingDetailDTO> findBillingPreviewByAdmissionId(@PathVariable("admissionId") String admissionId);
}