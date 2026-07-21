package kr.co.seoulit.his.billingservice.master.service;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.master.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.master.mapper.BillingMapper;
import kr.co.seoulit.his.billingservice.master.repository.BillingRepository;
import kr.co.seoulit.his.billingservice.master.dto.BillingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BillingMasterServiceImpl implements BillingMasterService {

    private final BillingRepository billingRepository;
    private final BillingMapper billingMapper;

    public BillingMasterServiceImpl(BillingRepository billingRepository, BillingMapper billingMapper) {
        this.billingRepository = billingRepository;
        this.billingMapper = billingMapper;
    }

    @Override
    public List<BillingDTO> getAllActiveBillingMasters() {
        List<BillingEntity> billingEntityList = billingRepository.findByUseYn("Y");
        return billingMapper.toDto(billingEntityList);
    }
    // 전체조회할때 use_yn="Y" 인것만 조회해서 불러옴.
    @Override
    public BillingDTO getBillingMasterById(String billingMasterId) {
        BillingEntity entity=billingRepository.findById(billingMasterId)   //jpa로 billing 식별자 조회
                .orElseThrow(()->new BusinessException(ErrorCode.BILLING_MASTER_NOT_BILLINGID));
                //존재하지 않는 아이디 -> "존재하지 않는 식별아이디" 알림
        return billingMapper.toDto(entity);
    }
    //단일 조회
    @Override
    public BillingDTO createBillingMaster(BillingDTO billingDTO) {
        // 필수값 체크 - DB에 not null 오류 방지 
    if (billingDTO.getSourceServiceCode() == null || billingDTO.getSourceServiceCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_SOURCE_SERVICE_CODE_NOT_FOUND);
    }
        //서비스 구분 코드
    if (billingDTO.getFeeCode() == null || billingDTO.getFeeCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND);
    }
        //수기 코드 
    if (billingDTO.getFeeName() == null || billingDTO.getFeeName().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_FEE_NAME_NOT_FOUND);
    } 
        //수기 명칭
    if (billingDTO.getDefaultPrice() == null) {
        throw new BusinessException(ErrorCode.BILLING_DEFAULT_PRICE_NOT_FOUND);
    } 
        //기본 단가 
    if (billingDTO.getCategoryCode() == null || billingDTO.getCategoryCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_CATEGORY_CODE_NOT_FOUND);
    }
        //분류 코드
    if (billingDTO.getEffectiveFrom() == null) {
        throw new BusinessException(ErrorCode.BILLING_EFFECTIVE_FORM_NOT_FOUND);
    }
        //적용 시작일
    BillingEntity entity = billingMapper.toEntity(billingDTO);

    // PK 생성
    entity.setBillingMasterId(UUID.randomUUID().toString());

    // 기본값
    entity.setUseYn("Y");
    // 받아온 데이터를 entity 형태로 DB에 저장
    BillingEntity savedEntity = billingRepository.save(entity);
    // 저장한 데이터를 DTO로 변환해서 반환. 
    return billingMapper.toDto(savedEntity);
    }

}
