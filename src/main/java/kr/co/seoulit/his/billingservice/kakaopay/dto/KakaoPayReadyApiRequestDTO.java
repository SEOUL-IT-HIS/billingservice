package kr.co.seoulit.his.billingservice.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 우리 백엔드 -> 카카오페이 실제 ready API (POST https://open-api.kakaopay.com/online/v1/payment/ready)
 * 카카오페이가 요구하는 JSON 필드명이 snake_case라서, 이 프로젝트 컨벤션(camelCase) 필드마다
 * @JsonProperty로 실제 전송될 이름을 붙여준다. RestTemplate이 이 객체를 JSON으로 바꿀 때
 * @JsonProperty 값을 키로 사용하므로, Getter/Setter 이름은 카카오페이와 무관하게 자유롭게 써도 된다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayReadyApiRequestDTO {

    @JsonProperty("cid")
    private String cid;                 // 가맹점 코드. 개발 단계에선 카카오페이가 제공하는 테스트 고정값 "TC0ONETIME" 사용,
                                         // 나중에 실제 가맹점 코드 발급받으면 그걸로 교체(application.properties로 뺄 값)

    @JsonProperty("partner_order_id")
    private String partnerOrderId;      // 가맹점 주문번호 - billingId를 그대로 사용

    @JsonProperty("partner_user_id")
    private String partnerUserId;       // 가맹점 회원 id - patientId를 그대로 사용

    @JsonProperty("item_name")
    private String itemName;            // 결제 화면에 보여줄 상품명 (ex. "진료비 수납")

    @JsonProperty("quantity")
    private Integer quantity;           // 수량 - 항목별 결제가 아니라 총액 한 번에 결제하는 거라 1로 고정해서 보냄

    @JsonProperty("total_amount")
    private Long totalAmount;           // 총 결제 금액 - billing_detail 조회해서 나온 totalAmount 사용 (프론트 값 아님)

    @JsonProperty("tax_free_amount")
    private Long taxFreeAmount;         // 비과세 금액 - 지금은 세금 분리 로직이 없어서 0으로 고정

    @JsonProperty("approval_url")
    private String approvalUrl;         // 결제 성공 시 카카오페이가 리다이렉트할 우리 쪽 콜백 URL (billingId 쿼리 포함)

    @JsonProperty("cancel_url")
    private String cancelUrl;           // 사용자가 결제 도중 취소했을 때 리다이렉트할 URL

    @JsonProperty("fail_url")
    private String failUrl;             // 결제 실패했을 때 리다이렉트할 URL
}
