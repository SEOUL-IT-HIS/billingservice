package kr.co.seoulit.his.billingservice.kakaopay.dto;

import lombok.*;

/**
 * 프론트 -> 우리 백엔드 (POST /api/billing/payment/kakaopay/approve)
 * 카카오페이 결제창에서 결제를 마치고 콜백 페이지로 돌아온 뒤, 그 화면이 넘겨주는 값.
 * - billingId: 어떤 수납 건을 승인하는지 (ready 때 보낸 것과 동일한 건)
 * - pgToken: 사용자가 실제로 카카오페이에서 결제를 완료했다는 증거 값.
 *   카카오페이가 콜백 URL의 쿼리스트링(pg_token)으로 붙여줌
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayApproveRequestDTO {

    private String billingId;
    private String pgToken;
}
