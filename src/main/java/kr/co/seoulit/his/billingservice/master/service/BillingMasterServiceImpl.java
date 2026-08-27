package kr.co.seoulit.his.billingservice.master.service;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.master.entity.BillingMasterEntity;
import kr.co.seoulit.his.billingservice.master.mapper.BillingMasterMapper;
import kr.co.seoulit.his.billingservice.master.repository.BillingMasterRepository;
import kr.co.seoulit.his.billingservice.master.dto.BillingMasterDTO;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BillingMasterServiceImpl implements BillingMasterService {

    private final BillingMasterRepository billingMasterRepository;
    private final BillingMasterMapper billingMasterMapper;

    public BillingMasterServiceImpl(BillingMasterRepository billingMasterRepository, BillingMasterMapper billingMasterMapper) {
        this.billingMasterRepository = billingMasterRepository;
        this.billingMasterMapper = billingMasterMapper;
    }
// 전체조회할때 use_yn="Y" 인것만 조회해서 불러옴.
    @Override
    public List<BillingMasterDTO> getAllActiveBillingMasters() {
        List<BillingMasterEntity> billingEntityList = billingMasterRepository.findByUseYn("Y");
        return billingMasterMapper.toDto(billingEntityList);
    }
//단일 조회
    @Override
    public BillingMasterDTO getBillingMasterById(String billingMasterId) {
        BillingMasterEntity entity=billingMasterRepository.findById(billingMasterId)   //jpa로 billing 식별자 조회
                .orElseThrow(()->new BusinessException(ErrorCode.BILLING_MASTER_NOT_BILLINGID));
                //존재하지 않는 아이디 -> "존재하지 않는 식별아이디" 알림
        return billingMasterMapper.toDto(entity);
    }
//생성
    @Override
    public BillingMasterDTO createBillingMaster(BillingMasterDTO billingMasterDTO) {
        // 필수값 체크 - DB에 not null 오류 방지
    if (billingMasterDTO.getSourceServiceCode() == null || billingMasterDTO.getSourceServiceCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_SOURCE_SERVICE_CODE_NOT_FOUND);
    }
        //서비스 구분 코드
    if (billingMasterDTO.getFeeCode() == null || billingMasterDTO.getFeeCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_FEE_CODE_NOT_FOUND);
    }
        //수기 코드
    if (billingMasterDTO.getFeeName() == null || billingMasterDTO.getFeeName().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_FEE_NAME_NOT_FOUND);
    }
        //수기 명칭
    if (billingMasterDTO.getDefaultPrice() == null
            || new BigDecimal(billingMasterDTO.getDefaultPrice()).signum() < 0) {
        throw new BusinessException(ErrorCode.BILLING_DEFAULT_PRICE_INVALID);
    }
        //기본 단가
    if (billingMasterDTO.getCategoryCode() == null || billingMasterDTO.getCategoryCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_CATEGORY_CODE_NOT_FOUND);
    }
        //분류 코드
    if (billingMasterDTO.getEffectiveFrom() == null) {
        throw new BusinessException(ErrorCode.BILLING_EFFECTIVE_FORM_NOT_FOUND);
    }
        //적용 종료일
    if (billingMasterDTO.getEffectiveTo() == null) {
        throw new BusinessException(ErrorCode.BILLING_EFFECTIVE_TO_NOT_FOUND);
    }
    if (billingMasterDTO.getInsuranceTypeCode()==null || billingMasterDTO.getInsuranceTypeCode().isBlank()){
        throw new BusinessException(ErrorCode.BILLING_INSURANCE_TYPE_CODE_NOT_FOUND);
    }
    //급여 비급여 코드


        //적용 시작일
    BillingMasterEntity entity = billingMasterMapper.toEntity(billingMasterDTO);

    // PK 생성
    entity.setBillingMasterId(UUID.randomUUID().toString());

    // 기본값
    entity.setUseYn("Y");
    // 받아온 데이터를 entity 형태로 DB에 저장
    BillingMasterEntity savedEntity;
    try {
        // saveAndFlush로 즉시 INSERT를 실행해야 유니크 제약 위반이 이 메서드 안에서(트랜잭션 커밋 시점이 아니라) 바로 발생한다
        savedEntity = billingMasterRepository.saveAndFlush(entity);
    } catch (DataIntegrityViolationException e) {
        // 서비스 구분 코드+수기 코드+적용 시작일 조합이 이미 등록된 경우 (UQ_BILLING_MASTER_FEE)
        throw new BusinessException(ErrorCode.BILLING_MASTER_DUPLICATED);
    }
    // 저장한 데이터를 DTO로 변환해서 반환.
    return billingMasterMapper.toDto(savedEntity);
    }

}
