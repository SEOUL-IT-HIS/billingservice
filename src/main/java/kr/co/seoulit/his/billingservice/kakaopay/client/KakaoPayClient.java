package kr.co.seoulit.his.billingservice.kakaopay.client;

import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApiApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApiApproveResponseDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyApiRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyApiResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor

public class KakaoPayClient {

    private final RestTemplate restTemplate;// RestTemplate은 Spring에서 제공하는 HTTP 요청/응답을 처리하는 클래스. KakaoPayClient는 RestTemplate을 통해 카카오페이 API와 통신

    @Value("${kakaopay.secret-key}")
    private String secretKey; // application.properties에 설정한 카카오페이 secret-key를 가져옴

    private static final String READY_URL="https://open-api.kakaopay.com/online/v1/payment/ready";
    // KakaoPayClient는 카카오페이 API의 "결제 준비" 엔드포인트 URL을 상수로 정의

    private static final String APPROVE_URL="https://open-api.kakaopay.com/online/v1/payment/approve";
    // "결제 승인" 엔드포인트 URL

    // 파라미터/리턴 타입 모두 "카카오페이와 직접 주고받는" DTO로 통일 - 우리 쪽 프론트용 DTO 변환은 service가 담당
    public KakaoPayReadyApiResponseDTO ready(KakaoPayReadyApiRequestDTO request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey); // 카카오페이 인증 방식

        HttpEntity<KakaoPayReadyApiRequestDTO> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(READY_URL, entity, KakaoPayReadyApiResponseDTO.class);
    }

    // ready랑 구조는 완전히 동일 - URL이랑 주고받는 DTO만 approve용으로 바뀜
    public KakaoPayApiApproveResponseDTO approve(KakaoPayApiApproveRequestDTO request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

        HttpEntity<KakaoPayApiApproveRequestDTO> entity = new HttpEntity<>(request, headers);

        return restTemplate.postForObject(APPROVE_URL, entity, KakaoPayApiApproveResponseDTO.class);
    }
}

