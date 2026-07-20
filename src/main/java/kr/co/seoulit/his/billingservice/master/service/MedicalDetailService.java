package kr.co.seoulit.his.billingservice.master.service;

import kr.co.seoulit.his.billingservice.master.dto.MedicalDetailDto;

import java.util.List;

public interface MedicalDetailService {

    /**
     * 수납기준정보 전체 조회
     */
    List<MedicalDetailDto> findAllMedicalDetails();

    /**
     * 사용 중인 수납기준정보 조회
     */
    List<MedicalDetailDto> findActiveMedicalDetails();

    /**
     * 수납기준정보 ID 단건 조회
     */
    MedicalDetailDto findMedicalDetail(String billingMasterId);

    /**
     * 서비스 코드와 수가 코드로 조회
     */
    MedicalDetailDto findByServiceCodeAndFeeCode(
            String sourceServiceCode,
            String feeCode
    );

    /**
     * 수납기준정보 등록
     */
    MedicalDetailDto createMedicalDetail(
            MedicalDetailDto medicalDetailDto
    );

    /**
     * 수납기준정보 수정
     */
    MedicalDetailDto updateMedicalDetail(
            String billingMasterId,
            MedicalDetailDto medicalDetailDto
    );

    /**
     * 사용 여부 변경
     */
    MedicalDetailDto updateUseYn(
            String billingMasterId,
            String useYn
    );

    /**
     * 삭제 처리
     *
     * USE_YN을 N으로 변경
     */
    void deleteMedicalDetail(String billingMasterId);
}