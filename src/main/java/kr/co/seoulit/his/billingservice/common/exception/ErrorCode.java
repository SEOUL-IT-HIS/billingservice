package kr.co.seoulit.his.billingservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    BILLING_MASTER_NOT_FOUND(HttpStatus.NOT_FOUND, "Billing master information not found."),
    // 청구 마스터 정보를 찾을 수 없습니다.
    BILLING_MASTER_DUPLICATED(HttpStatus.CONFLICT, "Billing master information already exists."),
    // 이미 존재하는 청구 마스터 정보입니다.
    BILLING_MASTER_NOT_BILLINGID(HttpStatus.NOT_FOUND, "Billing identifier does not exist."),
    // 존재하지 않는 수납 식별자입니다.
    BILLING_SOURCE_SERVICE_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Source service code not found."),
    // 서비스 구분 코드를 찾을수 없습니다.
    BILLING_FEE_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Fee code not found."),
    // 수기 코드를 찾을 수 없습니다.
    BILLING_FEE_NAME_NOT_FOUND(HttpStatus.BAD_REQUEST, "Fee name not found."),
    // 수기 명칭을 찾을 수 없습니다.
    BILLING_DEFAULT_PRICE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Default price must be 0 or greater."),
    // 단가가 0원 이상이 아닙니다.
    BILLING_CATEGORY_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Category code not found."),
    // 분류 코드를 찾을 수 없습니다.
    BILLING_EFFECTIVE_FORM_NOT_FOUND(HttpStatus.BAD_REQUEST, "Effective start date not found."),
    // 적용 시작일을 찾을 수 없습니다.
    BILLING_EFFECTIVE_TO_NOT_FOUND(HttpStatus.BAD_REQUEST, "Effective end date not found."),
    // 적용 종료일을 찾을 수 없습니다.
    BILLING_INSURANCE_TYPE_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "Insurance type code is required."),
    // 급여/비급여 코드는 필수입니다.
    BILLING_DEFAULT_PRICE_INVALID(HttpStatus.BAD_REQUEST, "Default price must be greater than or equal to 0."),
    // 기본 단가는 0원 이상이어야 합니다.
    BILLING_NOT_FOUND(HttpStatus.NOT_FOUND, "Billing information not found."),
    // 수납 정보를 찾을 수 없습니다.
    BILLING_ALREADY_PROCESSED(HttpStatus.CONFLICT, "Already processed or not in a payable state."),
    // 이미 처리되었거나 수납 가능한 상태가 아닙니다.
    BILLING_RECEPTION_OR_ADMISSION_ID_REQUIRED(HttpStatus.BAD_REQUEST, "Either reception ID or admission ID is required."),
    // 접수 ID 또는 입원 ID 중 하나는 필수입니다.
    PAYMENT_RECEIPT_NO_GENERATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate receipt number. Please try again."),
    // 영수증번호 채번에 실패했습니다. 다시 시도해주세요.

    KAKAOPAY_TID_NOT_FOUND(HttpStatus.NOT_FOUND, "Kakao Pay payment preparation information not found. Please retry from the ready step."),
    // 카카오페이 결제 준비 정보를 찾을 수 없습니다. ready부터 다시 시도해주세요.

    PATIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "Patient information not found."),
    // 환자 정보를 찾을 수 없습니다.
    PATIENT_SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "Unable to communicate with the patient service. Please try again later."),
    // 환자 서비스와 통신할 수 없습니다. 잠시 후 다시 시도해주세요.

    BILLING_DATE_FORMAT_INVALID(HttpStatus.BAD_REQUEST, "Invalid date format. (e.g., yyyy-MM-dd or yyyy-MM-dd'T'HH:mm:ss)"),
    // 날짜 형식이 올바르지 않습니다. (예: yyyy-MM-dd 또는 yyyy-MM-dd'T'HH:mm:ss)
    // <--생성일시,수정일시는 어느 서비스던 포함돼서 진정한 공통이라고 말할수있다.-->
    CREATED_AT_NOT_FOUND(HttpStatus.NOT_FOUND, "Created timestamp not found."),
    // 생성 일시를 찾을 수 없습니다.
    UPDATED_AT_NOT_FOUND(HttpStatus.NOT_FOUND, "Updated timestamp not found.");
    // 수정 일시를 찾을 수 없습니다.

    private final HttpStatus status;
    private final String message;
}
