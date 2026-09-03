package kr.co.seoulit.his.billingservice.settlement.consumer;

import kr.co.seoulit.his.billingservice.settlement.dto.DischargeRequestedEventDTO;
import kr.co.seoulit.his.billingservice.settlement.service.DischargeReadinessService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// 병동서비스가 퇴원 요청 시 발행하는 전용 토픽(구 방식). 실제 확인 로직은
// BillingChargeKafkaConsumer의 feeCode=DISCHARGE_REQUEST 분기와 함께 DischargeReadinessService를 공유한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class DischargeRequestedKafkaConsumer {

    private final DischargeReadinessService dischargeReadinessService;

    @KafkaListener(
            topics = "${billing.settlement.topic.discharge-requested}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = "spring.json.value.default.type=kr.co.seoulit.his.billingservice.settlement.dto.DischargeRequestedEventDTO"
    )
    public void consumeDischargeRequested(DischargeRequestedEventDTO event) {
        dischargeReadinessService.checkDischargeReadiness(event.getAdmissionId());
    }
}
