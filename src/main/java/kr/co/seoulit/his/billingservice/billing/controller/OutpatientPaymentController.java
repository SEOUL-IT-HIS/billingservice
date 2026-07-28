package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.billing.service.OutpatientPaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/billing/outpatient")
@RequiredArgsConstructor

public class OutpatientPaymentController{

    private final OutpatientPaymentService outpatientPaymentService;
}