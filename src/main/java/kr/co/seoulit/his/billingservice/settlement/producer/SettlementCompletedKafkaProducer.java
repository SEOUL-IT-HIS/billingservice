package kr.co.seoulit.his.billingservice.settlement.producer;

import kr.co.seoulit.his.billingservice.settlement.dto.SettlementCompletedEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

// 입원 건 수납이 실제로 완료(결제 SUCCESS)됐을 때 병동서비스에 발행. 금액 확정(READY)이 아니라
// 결제 완료 시점에만 쏜다 - 병동 쪽은 이 이벤트를 받아야 퇴원 처리를 진행하기 때문.
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SettlementCompletedKafkaProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${billing.settlement.topic.settlement-completed}")
    private String topic;

    public void publish(String admissionId) {
        kafkaTemplate.send(topic, admissionId, new SettlementCompletedEventDTO(admissionId));
    }
}
