package kr.co.seoulit.his.billingservice.settlement.consumer;

import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingRepository;
import kr.co.seoulit.his.billingservice.settlement.dto.DischargeRequestedEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// 병동서비스가 퇴원 요청 시 발행. 진료비는 입원 기간 내내 charge 등록 시점에 이미 admissionId 기준으로
// billing에 누적되고 있으므로, 여기서 별도로 집계/생성할 것은 없고 해당 admissionId의 billing이
// 실제로 존재하는지만 확인한다 (없으면 데이터 불일치이므로 경고 로그만 남김).
@Slf4j
@Component
@RequiredArgsConstructor
public class DischargeRequestedKafkaConsumer {

    private final BillingRepository billingRepository;

    @KafkaListener(
            topics = "${billing.settlement.topic.discharge-requested}",
            groupId = "${spring.kafka.consumer.group-id}",
            properties = "spring.json.value.default.type=kr.co.seoulit.his.billingservice.settlement.dto.DischargeRequestedEventDTO"
    )
    public void consumeDischargeRequested(DischargeRequestedEventDTO event) {
        BillingEntity billing = billingRepository.findByAdmissionId(event.getAdmissionId()).orElse(null);

        if (billing == null) {
            log.warn("퇴원 요청을 받았지만 해당 admissionId의 수납 정보가 없습니다: admissionId={}", event.getAdmissionId());
            return;
        }

        log.info("퇴원 요청 수신, 정산 준비 완료: admissionId={}, billingId={}, billingStatus={}",
                event.getAdmissionId(), billing.getBillingId(), billing.getBillingStatus());
    }
}
