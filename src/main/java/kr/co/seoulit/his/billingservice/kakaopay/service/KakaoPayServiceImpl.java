package kr.co.seoulit.his.billingservice.kakaopay.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import kr.co.seoulit.his.billingservice.billing.dto.PaymentRequestDTO;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailRepository;
import kr.co.seoulit.his.billingservice.billing.service.PaymentService;
import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.kakaopay.client.KakaoPayClient;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApiApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyApiRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyApiResponseDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Transactional
@RequiredArgsConstructor//생성자 주입

public class KakaoPayServiceImpl implements KakaoPayService {

    private final BillingDetailRepository billingDetailRepository;
    //새로 안 만들고 기존 거 재사용.
    private final KakaoPayClient kakaoPayClient;
    // KakaoPayClient를 주입받아 카카오페이 API 호출에 사용
    private final PaymentService paymentService;
    // approve 성공 후 billing_status 갱신 + Payment insert는 새로 안 만들고 기존 로직 재사용

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.approval-url}")
    private String approvalUrl;

    @Value("${kakaopay.cancel-url}")
    private String cancelUrl;

    @Value("${kakaopay.fail-url}")
    private String failUrl;

    // billingId -> tid 임시 저장소. approve 요청 때 다시 꺼내 써야 함.
    // 서버 재시작하면 날아가고, 인스턴스가 여러 대면 안 맞을 수 있음 - 나중에 DB 컬럼으로 옮기는 걸 고려해야 함
    private final Map<String, String> tidStore = new ConcurrentHashMap<>();

    @Override
    public KakaoPayReadyResponseDTO paymentBillingById(KakaoPayReadyRequestDTO request) {
        String billingId = request.getBillingId();

        // PaymentServiceImpl과 동일한 패턴: billingId 자체를 null 체크하는 게 아니라
        // "실제로 결제 가능한 건이 맞는지"를 DB 조회로 확인
        List<BillingDetailItemDTO> items = billingDetailRepository.findBillingDetailFull(billingId);
        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }
        BillingDetailItemDTO header = items.get(0);

        // 우리 백엔드용 요청을, 카카오페이가 요구하는 형태(KakaoPayReadyApiRequestDTO)로 조립
        KakaoPayReadyApiRequestDTO apiRequest = KakaoPayReadyApiRequestDTO.builder()
                .cid(cid)
                .partnerOrderId(billingId)
                .partnerUserId(header.getPatientId())
                .itemName("진료비 수납")
                .quantity(1)
                .totalAmount(header.getTotalAmount())
                .taxFreeAmount(0L)
                .approvalUrl(approvalUrl + "?billingId=" + billingId) // 콜백 페이지가 billingId를 쿼리로 읽어야 하므로 붙여서 보냄
                .cancelUrl(cancelUrl)
                .failUrl(failUrl)
                .build();

        // 실제 카카오페이 호출
        KakaoPayReadyApiResponseDTO apiResponse = kakaoPayClient.ready(apiRequest);

        // approve 때 다시 필요한 tid를 billingId 기준으로 저장해둠
        tidStore.put(billingId, apiResponse.getTid());

        // 프론트가 기대하는 응답 모양으로 변환해서 리턴
        return KakaoPayReadyResponseDTO.builder()
                .billingId(billingId)
                .redirectUrl(apiResponse.getNextRedirectPcUrl())
                .build();
    }

    @Override
    public void approve(KakaoPayApproveRequestDTO request) {
        String billingId = request.getBillingId();

        // ready 때 저장해둔 tid를 다시 꺼냄 - 없으면 ready를 건너뛰고 온 것이거나 서버가 재시작된 것
        String tid = tidStore.get(billingId);
        if (tid == null) {
            throw new BusinessException(ErrorCode.KAKAOPAY_TID_NOT_FOUND);
        }

        // ready 때와 동일하게 patientId 재조회 (partner_user_id는 ready 때 보낸 값과 같아야 함)
        List<BillingDetailItemDTO> items = billingDetailRepository.findBillingDetailFull(billingId);
        if (items.isEmpty()) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }
        BillingDetailItemDTO header = items.get(0);

        KakaoPayApiApproveRequestDTO apiRequest = KakaoPayApiApproveRequestDTO.builder()
                .cid(cid)
                .tid(tid)
                .partnerOrderId(billingId)
                .partnerUserId(header.getPatientId())
                .pgToken(request.getPgToken())
                .build();

        // 카카오페이가 승인을 거절하면 RestTemplate이 예외를 던지고, 그 예외가 그대로 위로 전파되어
        // 트랜잭션이 롤백됨(아래 processPayment까지 안 감) - 실패 시 우리 DB엔 아무 흔적도 안 남음
        kakaoPayClient.approve(apiRequest);

        // 카카오페이 승인 확인 끝났으니, 그 다음은 CASH/CARD와 완전히 동일한 마무리 로직 재사용
        paymentService.processPayment(new PaymentRequestDTO(billingId, "KAKAO_PAY"));

        tidStore.remove(billingId); // 다 썼으니 정리
    }

}
