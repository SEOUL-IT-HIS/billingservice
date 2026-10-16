package kr.co.seoulit.his.billingservice.kakaopay.controller;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayCancelIdDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyResponseDTO;
import kr.co.seoulit.his.billingservice.kakaopay.service.KakaoPayService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/billing/payment")
@RequiredArgsConstructor

public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    // 프론트가 billingId를 body(JSON)로 보내고, 응답으로 billingId + redirectUrl을 돌려받음
    // (프론트: apiClient.post("/api/billing/payment/ready", { billingId }))
    @PostMapping("/ready")
    public ResponseEntity<ApiResponse<KakaoPayReadyResponseDTO>> ready(@RequestBody KakaoPayReadyRequestDTO request) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        kakaoPayService.paymentBillingById(request)
                )
        );
    }

    // 프론트: KakaoPayReturn.tsx가 카카오페이 결제창에서 돌아온 직후 호출
    // (프론트: apiClient.post("/api/billing/payment/approve", { billingId, pgToken }))
    @PostMapping("/approve")
    public ResponseEntity<ApiResponse<Void>> approve(@RequestBody KakaoPayApproveRequestDTO request) {
        kakaoPayService.approve(request);
        return ResponseEntity.ok(
                ApiResponse.success(SuccessCode.OK.getMessage(), null)
        );
    }

    // TODO: 카카오페이 결제취소 미구현이라 API 노출하지 않도록 주석처리. 구현 시 아래 사항 반영 필요
    //  - approve 때 tid를 PAYMENT.APPROVAL_NO에 저장 (지금은 approve 후 tid가 삭제돼 취소에 쓸 수 없음)
    //  - KakaoPayClient.cancel() 추가, 요청 DTO에 paymentId 추가
//    @PatchMapping("/cancel")
//    public ResponseEntity<ApiResponse<KakaoPayCancelIdDTO>> cancel(
//            @RequestBody KakaoPayCancelIdDTO request
//    ){
//        kakaoPayService.cancel(request);
//        return ResponseEntity.ok(
//                ApiResponse.success(SuccessCode.OK.getMessage(), null)
//        );
//    }
}