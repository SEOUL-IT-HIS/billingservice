package kr.co.seoulit.his.billingservice.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 우리 백엔드 -> 카카오페이 실제 approve API (POST https://open-api.kakaopay.com/online/v1/payment/approve)
 * "아까 ready로 열어둔 결제건, 방금 사용자가 승인했으니 최종 확정해줘"를 알리는 호출.
 * ready 호출과 달리 금액/상품명은 다시 안 보낸다 - 카카오페이가 tid에 그 정보를 이미 갖고 있고,
 * 여기서 다시 보내면 오히려 위변조 여지만 생기기 때문.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayApiApproveRequestDTO {

    @JsonProperty("cid")
    private String cid;                 // 가맹점 코드 - ready 때 보낸 것과 반드시 동일해야 함 (테스트 고정값 "TC0ONETIME")

    @JsonProperty("tid")
    private String tid;                 // ready 응답으로 받았던 결제 건 고유번호. 어딘가(캐시/DB)에 billingId 기준으로
                                         // 저장해뒀다가 여기서 다시 꺼내 써야 함 - 이 DTO 스스로는 못 구함, service가 채워줌

    @JsonProperty("partner_order_id")
    private String partnerOrderId;      // ready 때 보낸 것과 동일한 값(billingId)

    @JsonProperty("partner_user_id")
    private String partnerUserId;       // ready 때 보낸 것과 동일한 값(patientId)

    @JsonProperty("pg_token")
    private String pgToken;             // 콜백 페이지가 우리 백엔드로 넘겨준 값을 그대로 전달
}
