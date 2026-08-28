package kr.co.seoulit.his.billingservice.inquiry.history.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingHistorySummaryDTO {

    private String billingId;
    private String patientId;

    // 환자 서비스에서 채움
    private String patientName;
    private String address;
    private String addressDetail;
    private String phoneNo;

    // 외래 / 입원 구분
    private String billingType;

    // 실제 결제 정보
    private String paymentId;
    private Integer paymentAmount;
    private String paymentMethod;
    private LocalDateTime paymentAt;

    private String billingStatus;
}