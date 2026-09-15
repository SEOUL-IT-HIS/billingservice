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
import kr.co.seoulit.his.billingservice.kakaopay.entity.KakaoPayReadyEntity;
import kr.co.seoulit.his.billingservice.kakaopay.repository.KakaoPayReadyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
    private final KakaoPayReadyRepository kakaoPayReadyRepository;
    // ready~approve 사이의 tid 보관용. 메모리(tidStore) 대신 DB로 관리해 서버 재시작/다중 인스턴스에도 안전하게 함

    @Value("${kakaopay.cid}")
    private String cid;

    @Value("${kakaopay.approval-url}")
    private String approvalUrl;

    @Value("${kakaopay.cancel-url}")
    private String cancelUrl;

    @Value("${kakaopay.fail-url}")
    private String failUrl;

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

        // approve 때 다시 필요한 tid를 billingId 기준으로 DB에 저장해둠
        kakaoPayReadyRepository.save(new KakaoPayReadyEntity(billingId, apiResponse.getTid(), LocalDateTime.now()));

        // 프론트가 기대하는 응답 모양으로 변환해서 리턴
        return KakaoPayReadyResponseDTO.builder()
                .billingId(billingId)
                .redirectUrl(apiResponse.getNextRedirectPcUrl())
                .build();
    }

    @Override
    public void approve(KakaoPayApproveRequestDTO request) {
        String billingId = request.getBillingId();

        // find + delete로 꺼냄. ConcurrentHashMap.remove()처럼 원자적이진 않아서, approve가
        // 거의 동시에 두 번 들어오면(예: React StrictMode 개발 모드 이펙트 중복 실행) 둘 다 delete
        // 전에 findById를 통과해 카카오페이 approve API가 중복 호출될 여지가 이론적으로 남아있음 -
        // "payment is already done!"(-702) 재발 가능성이 있으면 findById에 비관적 락을 추가할 것
        String tid = kakaoPayReadyRepository.findById(billingId)
                .map(KakaoPayReadyEntity::getTid)
                .orElseThrow(() -> new BusinessException(ErrorCode.KAKAOPAY_TID_NOT_FOUND));
        kakaoPayReadyRepository.deleteById(billingId);

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

        // 카카오페이가 승인을 거절하면 RestTemplate이 예외를 던지고, 메서드 전체가 @Transactional이라
        // 이 예외가 위로 전파되면서 앞의 deleteById까지 포함해 전부 롤백됨 - tid row가 그대로 남아
        // 재시도할 수 있으므로, 실패 시 tid를 다시 저장하는 별도 처리가 필요 없음
        kakaoPayClient.approve(apiRequest);

        // 카카오페이 승인 확인 끝났으니, 그 다음은 CASH/CARD와 완전히 동일한 마무리 로직 재사용
        paymentService.processPayment(new PaymentRequestDTO(billingId, "KAKAO_PAY"));
    }

}
