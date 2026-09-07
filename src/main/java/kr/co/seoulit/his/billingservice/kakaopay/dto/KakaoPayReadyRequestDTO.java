package kr.co.seoulit.his.billingservice.kakaopay.dto;

import lombok.*;

/**
 * 프론트 -> 우리 백엔드 (POST /api/billing/payment/kakaopay/ready)
 * billingId만 받는다. 금액/상품명 같은 실제 결제 정보는 서비스 레이어에서
 * billingId로 DB를 다시 조회해서 채운다 - 프론트가 보낸 금액을 그대로 믿지 않는다는
 * PaymentServiceImpl과 같은 원칙(BillingChargeServiceImpl 주석 참고).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayReadyRequestDTO {

    private String billingId;
}
