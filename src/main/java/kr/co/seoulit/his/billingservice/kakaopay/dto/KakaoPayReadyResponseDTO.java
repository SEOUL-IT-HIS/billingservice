package kr.co.seoulit.his.billingservice.kakaopay.dto;

import lombok.*;

/**
 * 우리 백엔드 -> 프론트 (ready 요청에 대한 응답)
 * 프론트는 redirectUrl로 카카오페이 결제창으로 리다이렉트한다.
 * billingId는 프론트가 이미 알고 있는 값이라 응답에 굳이 없어도 되지만,
 * 요청하신 대로 확인용으로 같이 내려준다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoPayReadyResponseDTO {

    private String billingId;
    private String redirectUrl;
}
