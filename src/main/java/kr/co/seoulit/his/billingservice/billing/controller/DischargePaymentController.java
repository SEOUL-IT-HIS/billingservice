package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.billing.service.DischargePaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing/discharge")
@RequiredArgsConstructor

public class DischargePaymentController{

    private final DischargePaymentService dischargePaymentService;
}