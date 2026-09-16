package kr.co.seoulit.his.billingservice.charge.service;

import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;

public interface BillingChargeService{

    void createCharge(BillingChargeRequestDTO billingChargeRequestDTO);
}