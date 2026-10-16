package kr.co.seoulit.his.billingservice.kakaopay.service;

import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayCancelIdDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyResponseDTO;

public interface KakaoPayService {

    KakaoPayReadyResponseDTO paymentBillingById(KakaoPayReadyRequestDTO request);

    void approve(KakaoPayApproveRequestDTO request);

    // TODO: 카카오페이 결제취소 미구현이라 주석처리
//    KakaoPayCancelIdDTO cancel(KakaoPayCancelIdDTO request);

}
