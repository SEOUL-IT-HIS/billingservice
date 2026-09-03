package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingStatusDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeResponseDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    List<BillingSummaryDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);
    //수납 담당자가 검색 조건(환자 이름 등)에 맞는 수납 건을 목록으로 조회

    // billingId 기준으로 수납 헤더(환자/구분ID/합계) + 상세 항목(검사/진료/약제 등)을 한 번에 조회.
    // billing_detail 행 개수만큼 여러 행이 나오고, 헤더 값(합계 등)은 윈도우 함수로 매 행에 동일하게 반복됨.
    List<BillingDetailItemDTO> findBillingDetailFull(@Param("billingId") String billingId);

    // 상태 변경 전 수납정보 존재 여부 확인
    BillingStatusDTO selectBillingDetailForStatusUpdate(@Param("billingId") String billingId);

    // READY 상태를 SUCCESS로 변경, 반환값은 실제로 변경된 행 수(0이면 이미 처리된 건)
    int updateBillingStatusToSuccess(@Param("billingId") String billingId);

    // 타 서비스에서 넘어온 수납 항목을 billing_detail에 등록
    void insertBillingDetail(BillingChargeResponseDTO billingChargeResponseDTO);
}