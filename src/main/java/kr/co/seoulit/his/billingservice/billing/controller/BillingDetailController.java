package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailDTO;
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



    //진료비 상세 조회 + 조건 검색
    @GetMapping
    public ResponseEntity<ApiResponse<List<BillingDetailDTO>>>searchBillingDetails(
            @RequestParam(required=false) String patientName){
        BillingDetailSearchDTO searchDTO=new BillingDetailSearchDTO();
        searchDTO.setPatientName(patientName);

        List<BillingDetailDTO> result=billingDetailService.searchBillingDetails(searchDTO);

        return ResponseEntity.ok(ApiResponse.success(SuccessCode.OK.getMessage(), result));
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

//    컨트롤러가 조회하는 방식이 잘못돼서 새로 작성
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