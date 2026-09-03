package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.billing.dto.*;
import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.billing.service.BillingDetailService;
import kr.co.seoulit.his.billingservice.billing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;

@Tag(name = "Billing Detail", description = "진료비 상세조회 및 수납처리 API")
@RestController
@RequestMapping("/api/billing/payment")
@RequiredArgsConstructor
@Validated

public class BillingDetailController {
    private final BillingDetailService billingDetailService;
    private final PaymentService paymentService;

    @Operation(summary = "환자 검색", description = "수납받을 환자를 검색합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingSummaryDTO>>> searchBillingDetails(
            @RequestParam(required = false) String patientName) {
        BillingDetailSearchDTO searchDTO = new BillingDetailSearchDTO();
        searchDTO.setPatientName(patientName);
        List<BillingSummaryDTO> result=
                billingDetailService.searchBillingDetails(searchDTO);
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        result
                )
        );
    }// 환자 이름으로 검색해서 환자의 간단한 기본정보 출력 - 중복된 이름 포함

    @Operation(summary = "환자 진료비 상세조회", description = "수납받을 환자의 진료비 상세조회")
    @GetMapping("/{billingId}")
    public ResponseEntity<ApiResponse<BillingDetailResponseDTO>> getBillingDetails(
            @PathVariable String billingId) {
        BillingDetailResponseDTO result =
                billingDetailService.getBillingDetails(billingId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        result
                )
        );
    }// 중복 포함 검색 결과된 리스트중 정보에 맞는 환자 찾고 진료비 상세조회

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<Void>> processPayment(
            @RequestBody PaymentRequestDTO request
    ){
        paymentService.processPayment(request);
        return ResponseEntity.ok(
                ApiResponse.success(

                        null
                )
        );
    }
    //결제 화면 - 팝업. 누르면 billing_status,payment_status 를 ready->success
}