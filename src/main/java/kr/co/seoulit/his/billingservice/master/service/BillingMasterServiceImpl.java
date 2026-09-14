package kr.co.seoulit.his.billingservice.master.service;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.master.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.master.mapper.BillingMapper;
import kr.co.seoulit.his.billingservice.master.repository.BillingRepository;
import kr.co.seoulit.his.billingservice.master.repository.CommonCodeRepository;
import kr.co.seoulit.his.billingservice.master.dto.BillingDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BillingMasterServiceImpl implements BillingMasterService {

    private final BillingRepository billingRepository;
    private final BillingMapper billingMapper;
    private final CommonCodeRepository commonCodeRepository;

    // 공통코드 "서비스구분" 그룹(외래시스템/응급시스템/병동시스템/검사시스템/수술시스템) - admin.common_code.group_id
    @Value("${billing.master.source-service-code.group-id}")
    private String sourceServiceCodeGroupId;

    // 공통코드 "수가분류코드" 그룹 - admin.common_code.group_id
    @Value("${billing.master.category-code.group-id}")
    private String categoryCodeGroupId;

    // 공통코드 "급여구분코드" 그룹 - admin.common_code.group_id
    @Value("${billing.master.insurance-type-code.group-id}")
    private String insuranceTypeCodeGroupId;

    public BillingMasterServiceImpl(BillingRepository billingRepository, BillingMapper billingMapper,
                                     CommonCodeRepository commonCodeRepository) {
        this.billingRepository = billingRepository;
        this.billingMapper = billingMapper;
        this.commonCodeRepository = commonCodeRepository;
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
    // FK(admin.common_code)를 타기 전에 미리 검증 - 위반 시 원시 DB 예외 대신 명확한 에러를 준다
    if (!commonCodeRepository.existsByCodeIdAndGroupIdAndUseYn(
            billingDTO.getSourceServiceCode(), sourceServiceCodeGroupId, "Y")) {
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
    if (!commonCodeRepository.existsByCodeIdAndGroupIdAndUseYn(
            billingDTO.getCategoryCode(), categoryCodeGroupId, "Y")) {
        throw new BusinessException(ErrorCode.BILLING_CATEGORY_CODE_NOT_FOUND);
    }
        //분류 코드
    if (billingDTO.getInsuranceTypeCode() == null || billingDTO.getInsuranceTypeCode().isBlank()) {
        throw new BusinessException(ErrorCode.BILLING_INSURANCE_TYPE_CODE_NOT_FOUND);
    }
    if (!commonCodeRepository.existsByCodeIdAndGroupIdAndUseYn(
            billingDTO.getInsuranceTypeCode(), insuranceTypeCodeGroupId, "Y")) {
        throw new BusinessException(ErrorCode.BILLING_INSURANCE_TYPE_CODE_NOT_FOUND);
    }
        //급여 비급여 코드
    if (billingDTO.getEffectiveFrom() == null) {
        throw new BusinessException(ErrorCode.BILLING_EFFECTIVE_FORM_NOT_FOUND);
    }
        //적용 시작일
    if (billingDTO.getEffectiveTo() == null) {
        throw new BusinessException(ErrorCode.BILLING_EFFECTIVE_TO_NOT_FOUND);
    }
        //적용 종료일
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
