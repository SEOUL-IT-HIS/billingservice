package kr.co.seoulit.his.billingservice.inquiry.history.dto;

import kr.co.seoulit.his.billingservice.billing.dto.BillingDetailItemDTO;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingHistoryDetailDTO {

    private String billingId;
    private String billingType;
    private String billingStatus;

    // 환자 서비스에서 채움
    private String patientId;
    private String patientName;
    private String phoneNo;
    private String birthDate;

    // 실제 결제 정보 (payment)
    private String paymentId;
    private Integer paymentAmount;
    private String paymentMethod;
    private LocalDateTime paymentAt;
    private String receiptNo;

    // 결제된 진료 항목 (billing_detail)
    private List<BillingDetailItemDTO> items;
}
// 수납이력 상세보기 - 결제 완료된 billing 한 건의 결제 정보 + 진료 항목
