package kr.co.seoulit.his.billingservice.kakaopay.service;

import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayApproveRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyRequestDTO;
import kr.co.seoulit.his.billingservice.kakaopay.dto.KakaoPayReadyResponseDTO;

public interface KakaoPayService {
    KakaoPayReadyResponseDTO paymentBillingById(KakaoPayReadyRequestDTO request);

    void approve(KakaoPayApproveRequestDTO request);
}
