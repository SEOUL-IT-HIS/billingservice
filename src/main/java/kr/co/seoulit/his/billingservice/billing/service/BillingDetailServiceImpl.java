package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

@Service
@Transactional
public class BillingDetailServiceImpl implements BillingDetailService {

    private final BillingDetailRepository billingDetailRepository;

    public BillingDetailServiceImpl(BillingDetailRepository billingDetailRepository) {
        this.billingDetailRepository = billingDetailRepository;
    }
    @Override
    public List<BillingSummaryDTO> searchBillingDetails(
            BillingDetailSearchDTO searchDTO
    ) {
        return billingDetailRepository.searchBillingDetails(searchDTO);
    }

    /**
     * 상세보기 버튼 클릭 후 billingId 기준 상세 항목 조회
     */
    @Override
    public List<BillingDetailDTO> getBillingDetailsByBillingId(
            String billingId
    ) {
        List<BillingDetailDTO> result =
                billingDetailRepository.findBillingDetailsByBillingId(billingId);

        if (result == null || result.isEmpty()) {
            throw new BusinessException(
                    ErrorCode.BILLING_MASTER_NOT_BILLINGID
            );
        }

        return result;
    }


    //상태값 변경
    @Override
    public void updateBillingStatusToSuccess(String billingId) {

        BillingDetailDTO billingDetailDTO =
                billingDetailRepository.selectBillingDetailForStatusUpdate(billingId);

        if (billingDetailDTO == null) {
            throw new BusinessException(
                    ErrorCode.BILLING_MASTER_NOT_BILLINGID);
        }
    }
    //수납 가능 여부 및 처리 상태 확인
    @Override
    public BillingDetailDTO getBillingStatus(String billingDetailId) {

        BillingDetailDTO billingDetailDTO =
                billingDetailRepository.findBillingStatusByDetailId(billingDetailId);

        if (billingDetailDTO == null) {throw new BusinessException(ErrorCode.BILLING_DETAIL_NOT_FOUND);}

        return billingDetailDTO;
    }
    //방문id로 조회
    @Override
    public List<BillingDetailDTO> getVisitBillingPreview(String visitId){

        List<BillingDetailDTO> billingDetails
                =billingDetailRepository.findBillingPreviewByVisitId(visitId);
        //service->repository->xml(sql)
        return billingDetails;
    }
    //입원id로 조회
    @Override
    public List<BillingDetailDTO> getAdmissionBillingPreview(String admissionId){

        List<BillingDetailDTO> billingDetails
                =billingDetailRepository.findBillingPreviewByAdmissionId(admissionId);
        //service->repository->xml(sql)
        return billingDetails;
    }




}