package kr.co.seoulit.his.billingservice.billing.service;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailRequestDto;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface BillingDetailService {

    List<BillingDetailResponseDto> searchDetails(String detailStatus, LocalDate from, LocalDate to);

    List<BillingDetailResponseDto> getDetailsByBillingId(String billingId);

    String createDetail(String billingId, BillingDetailRequestDto dto);

    void updateDetail(String detailId, BillingDetailRequestDto dto);

    void cancelDetail(String detailId);
}