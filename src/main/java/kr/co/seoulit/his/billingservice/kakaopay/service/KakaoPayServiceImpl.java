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
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayCancelIdDTO;
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
        List<String> billingIds = resolveBillingIds(request.getBillingIds(), request.getBillingId());
        // 여러 건을 묶어 결제해도 카카오페이 주문은 하나라서, 첫 번째 billingId를 대표 주문번호(tid 저장 키)로 사용
        String billingId = billingIds.get(0);

        // PaymentServiceImpl과 동일한 패턴: billingId 자체를 null 체크하는 게 아니라
        // "실제로 결제 가능한 건이 맞는지"를 DB 조회로 확인하고, 금액도 프론트 값이 아니라 DB 기준으로 합산
        String patientId = null;
        long totalAmount = 0L;
        for (String id : billingIds) {
            List<BillingDetailItemDTO> items = billingDetailRepository.findBillingDetailFull(id);
            if (items.isEmpty()) {
                throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
            }
            BillingDetailItemDTO header = items.get(0);
            if (patientId != null && !patientId.equals(header.getPatientId())) {
                // 한 번의 카카오페이 결제에 다른 환자의 건이 섞이면 안 됨
                throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
            }
            patientId = header.getPatientId();
            totalAmount += header.getTotalAmount();
        }

        // 우리 백엔드용 요청을, 카카오페이가 요구하는 형태(KakaoPayReadyApiRequestDTO)로 조립
        KakaoPayReadyApiRequestDTO apiRequest = KakaoPayReadyApiRequestDTO.builder()
                .cid(cid)
                .partnerOrderId(billingId)
                .partnerUserId(patientId)
                .itemName("진료비 수납")
                .quantity(1)
                .totalAmount(totalAmount)
                .taxFreeAmount(0L)
                // 콜백 페이지가 billingId(대표)와 billingIds(전체)를 쿼리로 읽어 approve에 그대로 보내야 하므로 붙여서 보냄
                .approvalUrl(approvalUrl + "?billingId=" + billingId + "&billingIds=" + String.join(",", billingIds))
                .cancelUrl(cancelUrl)
                .failUrl(failUrl)
                .build();

        // 실제 카카오페이 호출
        KakaoPayReadyApiResponseDTO apiResponse = kakaoPayClient.ready(apiRequest);

        // approve 때 다시 필요한 tid를 대표 billingId 기준으로 DB에 저장해둠
        kakaoPayReadyRepository.save(new KakaoPayReadyEntity(billingId, apiResponse.getTid(), LocalDateTime.now()));

        // 프론트가 기대하는 응답 모양으로 변환해서 리턴
        return KakaoPayReadyResponseDTO.builder()
                .billingId(billingId)
                .redirectUrl(apiResponse.getNextRedirectPcUrl())
                .build();
    }

    @Override
    public void approve(KakaoPayApproveRequestDTO request) {
        List<String> billingIds = resolveBillingIds(request.getBillingIds(), request.getBillingId());
        String billingId = billingIds.get(0); // ready 때 tid를 저장한 대표 billingId

        // find + delete로 꺼냄. 비관적 락(findByIdForUpdate)으로 조회하므로, approve가 거의
        // 동시에 두 번 들어와도(예: 콜백 페이지 새로고침) 두 번째 요청은 첫 번째 트랜잭션이
        // deleteById까지 끝날 때까지 대기했다가 빈 Optional을 받아 KAKAOPAY_TID_NOT_FOUND로
        // 끝나므로, 같은 tid로 카카오페이 approve API가 중복 호출되지 않는다.
        String tid = kakaoPayReadyRepository.findByIdForUpdate(billingId)
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
        paymentService.processPayment(new PaymentRequestDTO(billingIds, "KAKAO_PAY"));
    }

    // billingIds가 오면 그 목록 전체, 아니면 기존처럼 billingId 한 건
    private List<String> resolveBillingIds(List<String> billingIds, String billingId) {
        if (billingIds != null && !billingIds.isEmpty()) {
            return billingIds;
        }
        if (billingId == null) {
            throw new BusinessException(ErrorCode.BILLING_NOT_FOUND);
        }
        return List.of(billingId);
    }

    // TODO: 카카오페이 결제취소 API 호출 로직 구현 예정 (지금은 컴파일용 기본 검증만)
    @Override
    public KakaoPayCancelIdDTO cancel(KakaoPayCancelIdDTO request) {
        if (request.getPatientId() == null) {
            throw new BusinessException(ErrorCode.PATIENT_NOT_FOUND);
        }

        return request;
    }

}
