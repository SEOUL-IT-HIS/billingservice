package kr.co.seoulit.his.billingservice.inquiry.history.controller;

import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistorySearchDTO;
import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.inquiry.history.dto.BillingHistoryDTO;
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
    public ResponseEntity<ApiResponse<List<BillingHistoryDTO>>> getBillingHistory(
            @RequestParam String patientName
    ){
        BillingHistorySearchDTO searchDTO =
                BillingHistorySearchDTO.builder()
                        .patientName(patientName)
                        .build();
        List<BillingHistoryDTO> result=billingHistoryService.getBillingHistory(searchDTO);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK.getMessage(),result));
    }

    @GetMapping("/patinet/{patientId}")
    public ResponseEntity<ApiResponse<List<BillingHistoryDTO>>> getBillinghistoryByPatient(
             @PathVariable  String patientId
    ){
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        billingHistoryService.getBillinghistoryByPatient(patientId)
                )
        );
    }

}
