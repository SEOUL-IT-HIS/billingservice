package kr.co.seoulit.his.billingservice.master.service;

import kr.co.seoulit.his.billingservice.master.dto.MedicalDetailDto;
import kr.co.seoulit.his.billingservice.master.entity.MedicalDetail;
import kr.co.seoulit.his.billingservice.master.mapper.MedicalDetailMapper;
import kr.co.seoulit.his.billingservice.master.repository.MedicalDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalDetailServiceImpl implements MedicalDetailService {

    private final MedicalDetailRepository medicalDetailRepository;
    private final MedicalDetailMapper medicalDetailMapper;

    /**
     * 수납기준정보 전체 조회
     */
    @Override
    public List<MedicalDetailDto> findAllMedicalDetails() {

        return medicalDetailRepository
                .findAllByOrderByCreatedAtDesc()
                .stream()
                .map(medicalDetailMapper::toDto)
                .toList();
    }

    /**
     * 사용 중인 수납기준정보 조회
     */
    @Override
    public List<MedicalDetailDto> findActiveMedicalDetails() {

        return medicalDetailRepository
                .findByUseYnOrderByCreatedAtDesc("Y")
                .stream()
                .map(medicalDetailMapper::toDto)
                .toList();
    }

    /**
     * 수납기준정보 단건 조회
     */
    @Override
    public MedicalDetailDto findMedicalDetail(String billingMasterId) {

        MedicalDetail medicalDetail =
                findMedicalDetailEntity(billingMasterId);

        return medicalDetailMapper.toDto(medicalDetail);
    }

    /**
     * 서비스 코드와 수가 코드로 조회
     */
    @Override
    public MedicalDetailDto findByServiceCodeAndFeeCode(
            String sourceServiceCode,
            String feeCode
    ) {

        MedicalDetail medicalDetail =
                medicalDetailRepository
                        .findBySourceServiceCodeAndFeeCodeAndUseYn(
                                sourceServiceCode,
                                feeCode,
                                "Y"
                        )
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "등록된 수납기준정보가 없습니다. "
                                                + "sourceServiceCode="
                                                + sourceServiceCode
                                                + ", feeCode="
                                                + feeCode
                                )
                        );

        return medicalDetailMapper.toDto(medicalDetail);
    }

    /**
     * 수납기준정보 등록
     */
    @Override
    @Transactional
    public MedicalDetailDto createMedicalDetail(
            MedicalDetailDto medicalDetailDto
    ) {

        validateMedicalDetail(medicalDetailDto);

        boolean duplicated =
                medicalDetailRepository
                        .existsBySourceServiceCodeAndFeeCode(
                                medicalDetailDto.getSourceServiceCode(),
                                medicalDetailDto.getFeeCode()
                        );

        if (duplicated) {
            throw new IllegalArgumentException(
                    "이미 등록된 서비스 코드와 수가 코드입니다."
            );
        }

        LocalDateTime now = LocalDateTime.now();

        MedicalDetail medicalDetail =
                medicalDetailMapper.toEntity(medicalDetailDto)
                        .toBuilder()
                        .billingMasterId(createBillingMasterId())
                        .useYn(
                                normalizeUseYn(
                                        medicalDetailDto.getUseYn()
                                )
                        )
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        MedicalDetail savedMedicalDetail =
                medicalDetailRepository.save(medicalDetail);

        return medicalDetailMapper.toDto(savedMedicalDetail);
    }

    /**
     * 수납기준정보 수정
     */
    @Override
    @Transactional
    public MedicalDetailDto updateMedicalDetail(
            String billingMasterId,
            MedicalDetailDto medicalDetailDto
    ) {

        validateMedicalDetail(medicalDetailDto);

        MedicalDetail medicalDetail =
                findMedicalDetailEntity(billingMasterId);

        boolean duplicated =
                medicalDetailRepository
                        .existsBySourceServiceCodeAndFeeCodeAndBillingMasterIdNot(
                                medicalDetailDto.getSourceServiceCode(),
                                medicalDetailDto.getFeeCode(),
                                billingMasterId
                        );

        if (duplicated) {
            throw new IllegalArgumentException(
                    "이미 등록된 서비스 코드와 수가 코드입니다."
            );
        }

        medicalDetail.update(
                medicalDetailDto.getSourceServiceCode(),
                medicalDetailDto.getFeeCode(),
                medicalDetailDto.getFeeName(),
                medicalDetailDto.getDefaultPrice(),
                medicalDetailDto.getCategoryCode(),
                medicalDetailDto.getInsuranceTypeCode(),
                medicalDetailDto.getEffectiveFrom(),
                medicalDetailDto.getEffectiveTo(),
                normalizeUseYn(medicalDetailDto.getUseYn()),
                LocalDateTime.now()
        );

        /*
         * JPA 변경 감지 기능으로 UPDATE가 수행되므로
         * 별도의 save() 호출은 필수가 아닙니다.
         */
        return medicalDetailMapper.toDto(medicalDetail);
    }

    /**
     * 사용 여부 변경
     */
    @Override
    @Transactional
    public MedicalDetailDto updateUseYn(
            String billingMasterId,
            String useYn
    ) {

        MedicalDetail medicalDetail =
                findMedicalDetailEntity(billingMasterId);

        medicalDetail.changeUseYn(
                normalizeUseYn(useYn),
                LocalDateTime.now()
        );

        return medicalDetailMapper.toDto(medicalDetail);
    }

    /**
     * 수납기준정보 삭제 처리
     *
     * 진료비 상세나 수납내역에서 이미 참조할 수 있으므로
     * 물리적으로 삭제하지 않고 USE_YN을 N으로 변경합니다.
     */
    @Override
    @Transactional
    public void deleteMedicalDetail(String billingMasterId) {

        MedicalDetail medicalDetail =
                findMedicalDetailEntity(billingMasterId);

        medicalDetail.changeUseYn(
                "N",
                LocalDateTime.now()
        );
    }

    /**
     * Entity 공통 조회
     */
    private MedicalDetail findMedicalDetailEntity(
            String billingMasterId
    ) {

        return medicalDetailRepository
                .findById(billingMasterId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "수납기준정보를 찾을 수 없습니다. "
                                        + "billingMasterId="
                                        + billingMasterId
                        )
                );
    }

    /**
     * 필수 입력값 검증
     */
    private void validateMedicalDetail(
            MedicalDetailDto medicalDetailDto
    ) {

        if (medicalDetailDto == null) {
            throw new IllegalArgumentException(
                    "수납기준정보가 입력되지 않았습니다."
            );
        }

        if (isBlank(medicalDetailDto.getSourceServiceCode())) {
            throw new IllegalArgumentException(
                    "서비스 코드는 필수입니다."
            );
        }

        if (isBlank(medicalDetailDto.getFeeCode())) {
            throw new IllegalArgumentException(
                    "수가 코드는 필수입니다."
            );
        }

        if (isBlank(medicalDetailDto.getFeeName())) {
            throw new IllegalArgumentException(
                    "수가 명칭은 필수입니다."
            );
        }

        if (medicalDetailDto.getDefaultPrice() == null) {
            throw new IllegalArgumentException(
                    "기본 금액은 필수입니다."
            );
        }

        if (medicalDetailDto.getDefaultPrice().signum() < 0) {
            throw new IllegalArgumentException(
                    "기본 금액은 0원 이상이어야 합니다."
            );
        }

        if (medicalDetailDto.getEffectiveFrom() != null
                && medicalDetailDto.getEffectiveTo() != null
                && medicalDetailDto
                .getEffectiveTo()
                .isBefore(medicalDetailDto.getEffectiveFrom())) {

            throw new IllegalArgumentException(
                    "적용 종료일은 적용 시작일보다 빠를 수 없습니다."
            );
        }
    }

    /**
     * USE_YN 값 정리
     *
     * 값이 없으면 기본값 Y
     */
    private String normalizeUseYn(String useYn) {

        if (isBlank(useYn)) {
            return "Y";
        }

        String normalizedUseYn =
                useYn.trim().toUpperCase();

        if (!normalizedUseYn.equals("Y")
                && !normalizedUseYn.equals("N")) {

            throw new IllegalArgumentException(
                    "사용 여부는 Y 또는 N만 입력할 수 있습니다."
            );
        }

        return normalizedUseYn;
    }

    /**
     * 수납기준정보 ID 생성
     *
     * 예: BM-8A71E25502D1
     */
    private String createBillingMasterId() {

        return "BM-"
                + UUID.randomUUID()
                .toString()
                .replace("-", "")
                .substring(0, 12)
                .toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}