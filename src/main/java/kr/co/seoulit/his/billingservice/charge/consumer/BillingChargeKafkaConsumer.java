package kr.co.seoulit.his.billingservice.charge.consumer;

import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;
import kr.co.seoulit.his.billingservice.charge.service.BillingChargeService;
import kr.co.seoulit.his.billingservice.settlement.service.DischargeReadinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// REST(BillingChargeController)와 동일하게 createCharge()를 호출하는 또 다른 입구.
// 검사서비스/병동서비스 등 여러 서비스가 카프카로 넘긴 수납정보를 받아 처리한다.
// 메시지 모양(BillingChargeRequestDTO)과 처리 로직이 다 같아서, 토픽만 늘리고 컨슈머는 하나로 공유한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BillingChargeKafkaConsumer {

    // 병동서비스는 "퇴원신청" 신호를 실제 수가 대신 이 feeCode로 charge 등록 토픽에 함께 보낸다.
    // billing_master에 등록된 수가가 아니므로 createCharge()로 넘기면 BILLING_FEE_CODE_NOT_FOUND가 나며,
    // 애초에 청구 항목으로 적립할 대상도 아니라서 별도 분기로 처리한다.
    private static final String DISCHARGE_REQUEST_FEE_CODE = "DISCHARGE_REQUEST";

    private final BillingChargeService billingChargeService;
    private final DischargeReadinessService dischargeReadinessService;

    @KafkaListener(
            topics = {
                    "${billing.charge.topic.exam}",
                    "${billing.charge.topic.inpatient}"
            },
            groupId = "${spring.kafka.consumer.group-id}",
            properties = "spring.json.value.default.type=kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO"
    )
    public void consumeBillingCharge(BillingChargeRequestDTO request) {
        log.info("수납정보 수신: patientId={}, receptionId={}, admissionId={}, sourceServiceCode={}, feeCode={}",
                request.getPatientId(), request.getReceptionId(), request.getAdmissionId(),
                request.getSourceServiceCode(), request.getFeeCode());

        if (DISCHARGE_REQUEST_FEE_CODE.equals(request.getFeeCode())) {
            dischargeReadinessService.checkDischargeReadiness(request.getAdmissionId());
            return;
        }

        billingChargeService.createCharge(request);
    }
}
