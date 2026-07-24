package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
import kr.co.seoulit.his.billingservice.billing.service.BillingDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/billing")

public class BillingDetailController {
    private final BillingDetailService billingDetailService;

    public BillingDetailController(BillingDetailService billingDetailService){
        this.billingDetailService=billingDetailService;
    }
    //진료비 상세 전체 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingDetailDTO>>>getAllActiveBillingDetail(){
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        billingDetailService.getAllBillingDetails()
                )

        );
    }
    //진료비 상세 단일 조회
    @GetMapping("/{billingDetailId}")
    public ResponseEntity<ApiResponse<BillingDetailDTO>>getBillingDetail(@PathVariable String billingDetailId){
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.OK.getMessage(),
                        billingDetailService.getBillingDetailById(billingDetailId)
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

    //진료비 상세등록 - 병원 업무 특성상 다른 서비스에서 가져온 결과물을 산출해서 해야하기 때문에
    //등록이라는 시스템이 존재하지 않아야 하므로 조만간 삭제될 메서드.
    @PostMapping
    public ResponseEntity<ApiResponse<BillingDetailDTO>>createBillingDetail(
            @RequestBody BillingDetailDTO billingDeatilDTO){
        return ResponseEntity.ok(
                ApiResponse.success(
                        SuccessCode.CREATED.getMessage(),
                        billingDetailService.createBillingDetail(billingDeatilDTO)
                )
        );
    }

}
