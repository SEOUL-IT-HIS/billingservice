package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BillingDetailRepository {

    // 전체 조회
    List<BillingDetailDTO> findAllBillingDetails();

    // 상태별 조회
    List<BillingDetailDTO> findByDetailStatus(String detailStatus);

    // 단건 조회
    BillingDetailDTO findByBillingDetailId(String billingDetailId);

    // 등록
    int insertBillingDetail(BillingDetailDTO billingDetailDTO);
}