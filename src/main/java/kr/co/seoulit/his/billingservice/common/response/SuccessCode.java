package kr.co.seoulit.his.billingservice.common.response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SuccessCode {
    OK("요청이 성공했습니다."),
    CREATED("성공적으로 생성되었습니다."),
    UPDATED("성공적으로 수정되었습니다."),
    DELETED("성공적으로 삭제되었습니다.");

    private final String message;
}
