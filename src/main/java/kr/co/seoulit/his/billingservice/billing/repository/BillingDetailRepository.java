package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    List<BillingSummaryDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);
    //수납 담당자가 검색 조건(환자 이름 등)에 맞는 수납 건을 목록으로 조회

    BillingSummaryDTO findBillingSummaryByBillingId(String billingId);
    //검색 결과 리스트에서 해당 billingId의 결제 처리 화면용 대표 정보(수납 헤더/합계) 단건 조회

    // 상태별 조회
    List<BillingDetailDTO> findByDetailStatus(String detailStatus);

    // 수납 가능 여부 및 처리 상태 확인
    BillingDetailDTO findBillingStatusByDetailId(String billingDetailId);

    // 상태 변경 전 수납정보 존재 여부 확인
    BillingDetailDTO selectBillingDetailForStatusUpdate(@Param("billingId") String billingId);

    // READY 상태를 SUCCESS로 변경
    void updateBillingStatusToSuccess(@Param("billingId") String billingId);

    //↓방문/입원 id로 상세조회 SQL 쿼리 부르는 메서드
    List<BillingDetailDTO> findBillingPreviewByVisitId(@Param("visitId") String visitId);
    List<BillingDetailDTO> findBillingPreviewByAdmissionId(@Param("admissionId") String admissionId);
}