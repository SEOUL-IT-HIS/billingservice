package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingSummaryDTO;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailSearchDTO;
import kr.co.seoulit.his.billingservice.billing.service.BillingDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing/detail")
public class BillingDetailController {
    private final BillingDetailService billingDetailService;

    public BillingDetailController(BillingDetailService billingDetailService){
        this.billingDetailService=billingDetailService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingSummaryDTO>>> searchBillingDetails(
            @RequestParam(required = false) String patientName
    ) {
        BillingDetailSearchDTO searchDTO =
                new BillingDetailSearchDTO(patientName);
        List<BillingSummaryDTO> result=
                billingDetailService.searchBillingDetails(searchDTO);
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        result
                )
        );
    }
    @GetMapping("/{billingId}")
    public ResponseEntity<ApiResponse<List<BillingDetailDTO>>> getBillingDetails(
            @PathVariable String billingId
    ) {
        List<BillingDetailDTO> result =
                billingDetailService.getBillingDetailsByBillingId(billingId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        result
                )
        );
    }

    // 수납 가능 여부 및 처리 상태 확인.
    @GetMapping("/{billingDetailId}/billing-status")
    public ResponseEntity<ApiResponse<BillingDetailDTO>> getBillingStatus(
            @PathVariable String billingDetailId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        billingDetailService.getBillingStatus(billingDetailId)
                )
        );
    }
    // READY 상태를 SUCCESS로 변경 - 상태값 변경
    @PatchMapping("/{billingId}/billing-status")
    public ResponseEntity<ApiResponse<BillingDetailDTO>>updateBillingStatus(
            @PathVariable String billingId){
        billingDetailService.updateBillingStatusToSuccess(billingId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        null
                )
        );
    }

//    외래/입원 id를 기준으로 상세조회- 환자의 이의제기를 납득시킬 만한 상세 조회
    @GetMapping("/preview/visit/{visitId}")
    public ResponseEntity<ApiResponse<List<BillingDetailDTO>>>previewVistiId(
            @PathVariable String visitId){
        List<BillingDetailDTO> billingDetails=
        billingDetailService.getVisitBillingPreview(visitId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK.getMessage(), billingDetails));
    }
    @GetMapping("/preview/admission/{admissionId}")
    public ResponseEntity<ApiResponse<List<BillingDetailDTO>>>previewAdmissionId(
            @PathVariable String admissionId)
    {
        List<BillingDetailDTO> billingDetails=
                billingDetailService.getAdmissionBillingPreview(admissionId);
        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK.getMessage(), billingDetails));
    }




}