package kr.co.seoulit.his.billingservice.inquiry.history.controller;

import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;
import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDetailDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySummaryDTO;
import kr.co.seoulit.his.billingservice.inquiry.history.service.BillingHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/billing/history")
@RequiredArgsConstructor

public class BillingHistoryController {

    private final BillingHistoryService billingHistoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingHistorySummaryDTO>>> searchBillingHistory(
            @RequestParam String patientName
    ){
        BillingHistorySearchDTO searchDTO =
                BillingHistorySearchDTO.builder()
                        .patientName(patientName)
                        .build();
        List<BillingHistorySummaryDTO> result=billingHistoryService.searchBillingHistoryByName(searchDTO);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK.getMessage(),result));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<BillingHistoryDTO>>> getBillinghistory(
             @PathVariable  String patientId
    ){
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        billingHistoryService.getBillinghistoryByPatient(patientId)
                )
        );
    }

    // 수납이력 상세보기 - 결제 완료된 billing 한 건의 결제 정보 + 진료 항목
    @GetMapping("/{billingId}")
    public ResponseEntity<ApiResponse<BillingHistoryDetailDTO>> getBillingHistoryDetail(
            @PathVariable String billingId
    ){
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        billingHistoryService.getBillingHistoryDetail(billingId)
                )
        );
    }

}
