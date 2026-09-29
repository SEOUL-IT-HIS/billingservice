package kr.co.seoulit.his.billingservice.kakaopay.client;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApiApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApiApproveResponseDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyApiRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Slf4j
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

        try {
            return restTemplate.postForObject(READY_URL, entity, KakaoPayReadyApiResponseDTO.class);
        } catch (HttpStatusCodeException e) {
            // 카카오페이가 4xx/5xx로 거절한 경우 - 실제 원인(-703 등)은 응답 바디에 있으므로 로그로 남긴다
            log.error("카카오페이 ready 요청 거절 (partnerOrderId={}): status={}, body={}",
                    request.getPartnerOrderId(), e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException(ErrorCode.KAKAOPAY_API_ERROR);
        } catch (ResourceAccessException e) {
            // 타임아웃/연결 실패 등 네트워크 문제
            log.error("카카오페이 ready 요청 중 네트워크 오류 (partnerOrderId={}): {}",
                    request.getPartnerOrderId(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.KAKAOPAY_API_ERROR);
        }
    }

    // ready랑 구조는 완전히 동일 - URL이랑 주고받는 DTO만 approve용으로 바뀜
    public KakaoPayApiApproveResponseDTO approve(KakaoPayApiApproveRequestDTO request){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "SECRET_KEY " + secretKey);

        HttpEntity<KakaoPayApiApproveRequestDTO> entity = new HttpEntity<>(request, headers);

        try {
            return restTemplate.postForObject(APPROVE_URL, entity, KakaoPayApiApproveResponseDTO.class);
        } catch (HttpStatusCodeException e) {
            // 예: -702 "payment is already done!" 같은 카카오 쪽 거절 사유가 body에 담겨 온다
            log.error("카카오페이 approve 요청 거절 (partnerOrderId={}, tid={}): status={}, body={}",
                    request.getPartnerOrderId(), request.getTid(), e.getStatusCode(), e.getResponseBodyAsString(), e);
            throw new BusinessException(ErrorCode.KAKAOPAY_API_ERROR);
        } catch (ResourceAccessException e) {
            log.error("카카오페이 approve 요청 중 네트워크 오류 (partnerOrderId={}, tid={}): {}",
                    request.getPartnerOrderId(), request.getTid(), e.getMessage(), e);
            throw new BusinessException(ErrorCode.KAKAOPAY_API_ERROR);
        }
    }
}
