package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    // 조건 검색
    List<BillingDetailDTO> searchBillingDetails(BillingDetailSearchDTO searchDTO);

    // 상태별 조회
    List<BillingDetailDTO> findByDetailStatus(String detailStatus);

    // 단건 조회
    BillingDetailDTO findByBillingDetailId(String billingDetailId);

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