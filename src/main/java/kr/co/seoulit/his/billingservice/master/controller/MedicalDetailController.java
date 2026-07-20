package kr.co.seoulit.his.billingservice.master.controller;

import kr.co.seoulit.his.billingservice.master.dto.MedicalDetailDto;
import kr.co.seoulit.his.billingservice.master.service.MedicalDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/billing/master")
@RequiredArgsConstructor
public class MedicalDetailController {

    private final MedicalDetailService medicalDetailService;

    @GetMapping
    public ResponseEntity<List<MedicalDetailDto>> findAllMedicalDetails() {

        List<MedicalDetailDto> medicalDetails =
                medicalDetailService.findAllMedicalDetails();

        return ResponseEntity.ok(medicalDetails);
    }
    //수납기준정보 전체 조회//

    @GetMapping("/active")
    public ResponseEntity<List<MedicalDetailDto>> findActiveMedicalDetails() {

        List<MedicalDetailDto> medicalDetails =
                medicalDetailService.findActiveMedicalDetails();

        return ResponseEntity.ok(medicalDetails);
    }
    //사용 중인 수납기준정보 조회


    @GetMapping("/{billingMasterId}")
    public ResponseEntity<MedicalDetailDto> findMedicalDetail(
            @PathVariable String billingMasterId
    ) {

        MedicalDetailDto medicalDetail =
                medicalDetailService.findMedicalDetail(billingMasterId);

        return ResponseEntity.ok(medicalDetail);
    }
    //수납기준정보 단건 조회

    @GetMapping("/search")
    public ResponseEntity<MedicalDetailDto> findByServiceCodeAndFeeCode(
            @RequestParam String sourceServiceCode,
            @RequestParam String feeCode
    ) {

        MedicalDetailDto medicalDetail =
                medicalDetailService.findByServiceCodeAndFeeCode(
                        sourceServiceCode,
                        feeCode
                );

        return ResponseEntity.ok(medicalDetail);
    }

    //서비스 코드와 수가 코드로 수납기준정보 조회

    @PostMapping
    public ResponseEntity<MedicalDetailDto> createMedicalDetail(
            @RequestBody MedicalDetailDto medicalDetailDto
    ) {

        MedicalDetailDto createdMedicalDetail =
                medicalDetailService.createMedicalDetail(medicalDetailDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdMedicalDetail);
    }

    //수납기준정보 등록

    @PutMapping("/{billingMasterId}")
    public ResponseEntity<MedicalDetailDto> updateMedicalDetail(
            @PathVariable String billingMasterId,
            @RequestBody MedicalDetailDto medicalDetailDto
    ) {

        MedicalDetailDto updatedMedicalDetail =
                medicalDetailService.updateMedicalDetail(
                        billingMasterId,
                        medicalDetailDto
                );

        return ResponseEntity.ok(updatedMedicalDetail);
    }
    //수납기준정보 수정

    @PatchMapping("/{billingMasterId}/use")
    public ResponseEntity<MedicalDetailDto> updateUseYn(
            @PathVariable String billingMasterId,
            @RequestParam String useYn
    ) {

        MedicalDetailDto updatedMedicalDetail =
                medicalDetailService.updateUseYn(billingMasterId, useYn);

        return ResponseEntity.ok(updatedMedicalDetail);
    }
    //수납기준정보 사용 여부 변경
//삭제라서
//    @DeleteMapping("/{billingMasterId}")
//    public ResponseEntity<Void> deleteMedicalDetail(
//            @PathVariable String billingMasterId
//    ) {
//
//        medicalDetailService.deleteMedicalDetail(billingMasterId);
//
//        return ResponseEntity.noContent().build();
//    }
}