package kr.co.seoulit.his.billingservice.master.service;

import kr.co.seoulit.his.billingservice.master.dto.BillingMasterDTO;

import java.util.List;

public interface BillingMasterService {

    List<BillingMasterDTO> getAllActiveBillingMasters();

    BillingMasterDTO getBillingMasterById(String billingMasterId);

    BillingMasterDTO createBillingMaster(BillingMasterDTO billingDTO);

    // 수가명/기본단가/적용기간/사용여부만 수정 (코드·분류·보험유형은 수정 불가)
    BillingMasterDTO updateBillingMaster(String billingMasterId, BillingMasterDTO billingDTO);

    // ※ 수정,삭제 - 임의로 변경하거나 삭제 지양 
    // 권한 있는 관리자만 변경
    // 승인 또는 검토 절차 적용 
    // 변경 전·후 값과 작업자 기록
    // 기존 데이터를 덮어쓰기보다 새 버전 등록 
}
