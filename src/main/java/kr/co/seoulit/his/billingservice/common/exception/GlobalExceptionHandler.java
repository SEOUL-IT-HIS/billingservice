package kr.co.seoulit.his.billingservice.common.exception;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        HttpStatus status = e.getErrorCode().getStatus();
        return ResponseEntity.status(status)
                .body(ApiResponse.fail(status.value(), e.getMessage()));
    }
}
