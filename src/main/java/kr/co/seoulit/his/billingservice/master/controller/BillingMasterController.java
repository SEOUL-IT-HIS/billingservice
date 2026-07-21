package kr.co.seoulit.his.billingservice.master.controller;

import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import kr.co.seoulit.his.billingservice.common.response.SuccessCode;
import kr.co.seoulit.his.billingservice.master.dto.BillingDTO;
import kr.co.seoulit.his.billingservice.master.service.BillingMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billing/master")

public class BillingMasterController{

        private final BillingMasterService billingMasterService;

        public BillingMasterController(BillingMasterService billingMasterService){
            this.billingMasterService=billingMasterService;
        }
        //전체조회 - 데이터 많아지면 조건조회로 변경할 예정.
        @GetMapping
        public ResponseEntity<ApiResponse<List<BillingDTO>>> getAllActiveBillingMasters(){
            return ResponseEntity.ok(
                ApiResponse.success(
                    SuccessCode.OK.getMessage(),
                    billingMasterService.getAllActiveBillingMasters()
                )
            );
        }
        //수납식별 아이디를 가져와서 단일 조회  
        @GetMapping("/{billingMasterId}")
        public ResponseEntity<ApiResponse<BillingDTO>> getBillingMaster(@PathVariable String billingMasterId){
            return ResponseEntity.ok(
                ApiResponse.success(
                    SuccessCode.OK.getMessage(),
                    billingMasterService.getBillingMasterById(billingMasterId)
                )
            );
        }
        //수납 기준정보 생성-http요청 본문 내용을 자바 객체로 변환. 
        @PostMapping
        public ResponseEntity<ApiResponse<BillingDTO>> createBillingMaster(
                            @RequestBody BillingDTO billingDTO) {
            return ResponseEntity.ok(
            ApiResponse.success(
                    SuccessCode.CREATED.getMessage(),
                    billingMasterService.createBillingMaster(billingDTO)
            )
    );
}


}
