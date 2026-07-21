package kr.co.seoulit.his.billingservice.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    BILLING_MASTER_NOT_FOUND(HttpStatus.NOT_FOUND, "청구 마스터 정보를 찾을 수 없습니다."),
    BILLING_MASTER_DUPLICATED(HttpStatus.CONFLICT, "이미 존재하는 청구 마스터 정보입니다."),
    BILLING_MASTER_NOT_BILLINGID(HttpStatus.NOT_FOUND,"존재하지 않는 수납 식별자입니다."),
    BILLING_SOURCE_SERVICE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND,"서비스 구분 코드를 찾을수 없습니다."),
    BILLING_FEE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND,"수기 코드를 찾을 수 없습니다."),
    BILLING_FEE_NAME_NOT_FOUND(HttpStatus.NOT_FOUND,"수기 명칭을 찾을 수 없습니다."),
    BILLING_DEFAULT_PRICE_NOT_FOUND(HttpStatus.NOT_FOUND,"기본 단가를 찾을 수 없습니다."),
    BILLING_CATEGORY_CODE_NOT_FOUND(HttpStatus.NOT_FOUND,"분류 코드를 찾을 수 없습니다."),
    BILLING_EFFECTIVE_FORM_NOT_FOUND(HttpStatus.NOT_FOUND,"적용 시작일을 찾을 수 없습니다."),

    // <--생성일시,수정일시는 어느 서비스던 포함돼서 진정한 공통이라고 말할수있다.-->
    CREATED_AT_NOT_FOUND(HttpStatus.NOT_FOUND,"생성 일시를 찾을 수 없습니다."),
    UPDATED_AT_NOT_FOUND(HttpStatus.NOT_FOUND,"수정 일시를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
