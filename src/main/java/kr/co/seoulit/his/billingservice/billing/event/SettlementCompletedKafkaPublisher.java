package kr.co.seoulit.his.billingservice.billing.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

// 병동서비스는 settlement.completed 를 받으면 해당 입원 건을 퇴원 처리(DISCHARGED)한다.
// 결제 트랜잭션 안에서 바로 보내면 DB가 롤백돼도 메시지는 이미 나가서, 결제 안 된 환자가 퇴원 처리될 수 있다.
// 그래서 커밋이 끝난 뒤(AFTER_COMMIT)에만 발행한다.
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class SettlementCompletedKafkaPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${billing.settlement.topic.completed}")
    private String settlementCompletedTopic;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void publish(SettlementCompletedEvent event) {
        // 같은 입원 건의 메시지가 같은 파티션으로 가도록 admissionId를 key로 쓴다.
        // 결제는 이미 커밋된 뒤라 발행 실패로 결제를 되돌릴 수는 없음 - 검사서비스와 동일하게 재시도 없이 로그만 남긴다.
        kafkaTemplate.send(settlementCompletedTopic, event.getAdmissionId(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("퇴원정산 완료 발행 실패: admissionId={}", event.getAdmissionId(), ex);
                    } else {
                        log.info("퇴원정산 완료 발행: admissionId={}", event.getAdmissionId());
                    }
                });
    }
}
