package kr.co.seoulit.his.billingservice.billing.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingDetailItemDTO{
    @JsonIgnore
    private String billingId; //헤더(BillingDetailResponseDTO) 조립용 내부값 - 응답에는 노출하지 않음
    @JsonIgnore
    private String patientId; //환자서비스에서 patientName 조회하기 위한 내부용 값 - 응답에는 노출하지 않음
    @JsonIgnore
    private String receptionId;
    @JsonIgnore
    private String admissionId;
    @JsonIgnore
    private String billingStatus;
    @JsonIgnore
    private Long totalAmount;
    //위 6개는 전부 헤더 정보라 행마다 똑같은 값이 반복됨(윈도우 함수 결과) - 서비스에서 첫 행만 꺼내 헤더 조립하는 용도

    private String billingType;
    private String quantity;
    private String unitPrice;
    private String amount;
    private String detailStatus;
    private String occurredAt;

    private String feeCode;
    private String itemName;

}
// 환자의 진료비 상세조회 에서 진료(외래)별/입원별 상세조회