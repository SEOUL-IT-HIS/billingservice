package kr.co.seoulit.his.billingservice.insurance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BillingMasterController {

    @GetMapping("/billing-master/test")
    public String test() {
        return "Billing Master Controller Test";
    }
}