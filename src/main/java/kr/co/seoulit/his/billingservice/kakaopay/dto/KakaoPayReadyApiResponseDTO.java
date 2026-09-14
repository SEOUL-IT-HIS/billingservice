package kr.co.seoulit.his.billingservice.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 카카오페이 -> 우리 백엔드 (ready API 응답)
 * tid는 approve 때 다시 필요한 값이라 service 단에서 billingId 기준으로 저장해둬야 함.
 * next_redirect_pc_url을 우리 KakaoPayReadyResponseDTO의 redirectUrl로 옮겨 담아서 프론트에 내려줌.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true) // 카카오가 응답에 필드를 추가해도 여기 없으면 무시하고 넘어감
public class KakaoPayReadyApiResponseDTO {

    @JsonProperty("tid")
    private String tid;                     // 결제 건 고유번호 - approve 호출 때 반드시 다시 필요

    @JsonProperty("next_redirect_pc_url")
    private String nextRedirectPcUrl;       // PC 웹에서 이동시킬 결제창 주소

    @JsonProperty("next_redirect_mobile_url")
    private String nextRedirectMobileUrl;   // 모바일 웹에서 이동시킬 결제창 주소

    @JsonProperty("next_redirect_app_url")
    private String nextRedirectAppUrl;      // 앱에서 이동시킬 결제창 주소

    @JsonProperty("created_at")
    private String createdAt;               // 결제 준비 요청 시각
}
