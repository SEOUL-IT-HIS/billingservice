package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BillingDetailServiceImpl implements BillingDetailService {

    private final BillingDetailRepository billingDetailRepository;

    public BillingDetailServiceImpl(BillingDetailRepository billingDetailRepository) {
        this.billingDetailRepository = billingDetailRepository;
    }

    // 전체 조회
    @Override
    public List<BillingDetailDTO> getAllBillingDetails() {
        return billingDetailRepository.findAllBillingDetails();
    }

    // 단일 조회
    @Override
    public BillingDetailDTO getBillingDetailById(String billingDetailId) {
        BillingDetailDTO billingDetailDTO = billingDetailRepository.findByBillingDetailId(billingDetailId);
        if (billingDetailDTO == null) {
            // 존재하지 않는 아이디 -> "존재하지 않는 식별아이디" 알림
            throw new BusinessException(ErrorCode.BILLING_MASTER_NOT_BILLINGID);
        }
        return billingDetailDTO;
    }

    // 진료비 상세 등록
    @Override
    public BillingDetailDTO createBillingDetail(BillingDetailDTO billingDetailDTO) {
        // 필수값 체크 - DB에 not null 오류 방지
        if (billingDetailDTO.getSourceServiceCode() == null || billingDetailDTO.getSourceServiceCode().isBlank()) {
            throw new BusinessException(ErrorCode.BILLING_SOURCE_SERVICE_CODE_NOT_FOUND);}
        if (billingDetailDTO.getFeeCode() == null || billingDetailDTO.getFeeCode().isBlank()) {
            throw new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND);}
        if (billingDetailDTO.getBillingMasterId() == null || billingDetailDTO.getBillingMasterId().isBlank()){
            throw new BusinessException(ErrorCode.BILLING_MASTER_NOT_BILLINGID);}
        if (billingDetailDTO.getAmount() == null|| billingDetailDTO.getAmount().isBlank()){
            throw new BusinessException(ErrorCode.BILLING_DEFAULT_PRICE_NOT_FOUND);}
        // PK 자동 생성
        billingDetailDTO.setBillingDetailId(UUID.randomUUID().toString());

        // 기본 상태
        billingDetailDTO.setDetailStatus("READY");

        // 생성일시, 수정일시
        LocalDateTime now = LocalDateTime.now();
        billingDetailDTO.setCreatedAt(now);
        billingDetailDTO.setUpdatedAt(now);

        // DB 등록
        billingDetailRepository.insertBillingDetail(billingDetailDTO);

        return billingDetailDTO;
    }
}