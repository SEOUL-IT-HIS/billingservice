package kr.co.seoulit.his.billingservice.billing.service.impl;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailRequestDto;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDto;
import kr.co.seoulit.his.billingservice.billing.repository.BillingDetailMapper;
import kr.co.seoulit.his.billingservice.billing.service.BillingDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BillingDetailServiceImpl implements BillingDetailService {

    private final BillingDetailMapper billingDetailMapper;

    @Override
    public List<BillingDetailResponseDto> searchDetails(String detailStatus, LocalDate from, LocalDate to) {
        return billingDetailMapper.searchDetails(detailStatus, from, to);
    }

    @Override
    public List<BillingDetailResponseDto> getDetailsByBillingId(String billingId) {
        return billingDetailMapper.selectDetailsByBillingId(billingId);
    }

    @Override
    public String createDetail(String billingId, BillingDetailRequestDto dto) {
        String detailId = "BD" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        billingDetailMapper.insertDetail(billingId, detailId, dto);
        return detailId;
    }

    @Override
    public void updateDetail(String detailId, BillingDetailRequestDto dto) {
        billingDetailMapper.updateDetail(detailId, dto);
    }

    @Override
    public void cancelDetail(String detailId) {
        billingDetailMapper.updateStatus(detailId, "CANCELLED");
    }
}