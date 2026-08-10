package kr.co.seoulit.his.billingservice.charge.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import kr.co.seoulit.his.billingservice.charge.service.BillingChargeService;
import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Billing Charge", description = "타 서비스에서 수납정보 등록용 API")
@RestController
@RequestMapping("/api/billing/charge")
@RequiredArgsConstructor

public class BillingChargeController{

    private final BillingChargeService billingChargeService;

    @Operation(summary = "수납 정보 등록",
            description =
                    "각자 서비스에서 수납정보를 등록합니다." )
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCharge(
            @RequestBody BillingChargeRequestDTO request)
    {
        billingChargeService.createCharge(request);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK.getMessage(), null));
    }
}