package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingStatusDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    List<BillingSummaryDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);
    //수납 담당자가 검색 조건(환자 이름 등)에 맞는 수납 건을 목록으로 조회

    BillingDetailResponseDTO findBillingSummaryByBillingId(String billingId);
    //검색 결과 리스트에서 해당 billingId의 결제 처리 화면용 대표 정보(수납 헤더/합계) 단건 조회

    // 수납 가능 여부 및 처리 상태 확인
    BillingStatusDTO findBillingStatusByDetailId(String billingDetailId);

    // 상태 변경 전 수납정보 존재 여부 확인
    BillingStatusDTO selectBillingDetailForStatusUpdate(@Param("billingId") String billingId);

    // READY 상태를 SUCCESS로 변경, 반환값은 실제로 변경된 행 수(0이면 이미 처리된 건)
    int updateBillingStatusToSuccess(@Param("billingId") String billingId);

    //↓방문/입원 id로 상세조회 SQL 쿼리 부르는 메서드
    List<BillingDetailItemDTO> findBillingPreviewByVisitId(@Param("visitId") String visitId);
    List<BillingDetailItemDTO> findBillingPreviewByAdmissionId(@Param("admissionId") String admissionId);
}