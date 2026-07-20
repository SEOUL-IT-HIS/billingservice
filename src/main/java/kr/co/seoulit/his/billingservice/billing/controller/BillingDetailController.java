package kr.co.seoulit.his.billingservice.billing.controller;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailRequestDto;
import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailResponseDto;
import kr.co.seoulit.his.billingservice.billing.service.BillingDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/billing")
@RequiredArgsConstructor
public class BillingDetailController {

    private final BillingDetailService billingDetailService;  // 인터페이스로 선언!

    @GetMapping("/details")
    public ResponseEntity<List<BillingDetailResponseDto>> searchDetails(
            @RequestParam(required = false) String detailStatus,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(billingDetailService.searchDetails(detailStatus, from, to));
    }

    @GetMapping("/{billingId}/details")
    public ResponseEntity<List<BillingDetailResponseDto>> getDetails(
            @PathVariable String billingId) {
        return ResponseEntity.ok(billingDetailService.getDetailsByBillingId(billingId));
    }

    @PostMapping("/{billingId}/details")
    public ResponseEntity<String> createDetail(
            @PathVariable String billingId,
            @RequestBody BillingDetailRequestDto dto) {
        return ResponseEntity.ok(billingDetailService.createDetail(billingId, dto));
    }

    @PutMapping("/details/{detailId}")
    public ResponseEntity<Void> updateDetail(
            @PathVariable String detailId,
            @RequestBody BillingDetailRequestDto dto) {
        billingDetailService.updateDetail(detailId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/details/{detailId}")
    public ResponseEntity<Void> cancelDetail(@PathVariable String detailId) {
        billingDetailService.cancelDetail(detailId);
        return ResponseEntity.ok().build();
    }
}