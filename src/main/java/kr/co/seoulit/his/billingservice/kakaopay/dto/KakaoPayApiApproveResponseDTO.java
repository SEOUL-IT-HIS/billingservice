package kr.co.seoulit.his.billingservice.kakaopay.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 카카오페이 -> 우리 백엔드 (approve API 응답)
 * 우리 쪽에서 실제로 쓰는 건 "정상 응답이 왔다(=승인 성공)"는 사실 자체라서,
 * 필드는 로그/추후 확인용으로 최소한만 받아둔다.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class KakaoPayApiApproveResponseDTO {

    @JsonProperty("aid")
    private String aid;         // 이 승인 건의 고유번호

    @JsonProperty("tid")
    private String tid;

    @JsonProperty("approved_at")
    private String approvedAt;  // 카카오페이 쪽 실제 승인 시각
}
