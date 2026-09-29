package kr.co.seoulit.his.billingservice.common.exception;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        HttpStatus status = e.getErrorCode().getStatus();
        return ResponseEntity.status(status)
                .body(ApiResponse.fail(status.value(), e.getMessage()));
    }

    // BusinessException으로 분류되지 않은 예외(예: ORA-12516 같은 DB 세션 한도 초과, NPE 등)가
    // 터지면 지금까지는 아무 로그도 없이 맨몸 500만 나갔다. 최소한 서버 로그에 스택트레이스는
    // 남겨서, "왜 500인지" 다음에는 바로 확인할 수 있도록 한다.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpectedException(Exception e) {
        log.error("예상하지 못한 예외 발생", e);
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(ApiResponse.fail(status.value(), "일시적인 서버 오류입니다. 잠시 후 다시 시도해주세요."));
    }
}
