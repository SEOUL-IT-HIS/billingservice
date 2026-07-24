package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    // 전체 조회
    List<BillingDetailDTO> findAllBillingDetails();

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

    // 등록
    int insertBillingDetail(BillingDetailDTO billingDetailDTO);
}