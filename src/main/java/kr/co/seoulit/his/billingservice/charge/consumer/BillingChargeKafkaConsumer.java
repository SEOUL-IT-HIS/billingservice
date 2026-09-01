package kr.co.seoulit.his.billingservice.charge.consumer;

import kr.co.seoulit.his.billingservice.charge.dto.BillingChargeRequestDTO;
import kr.co.seoulit.his.billingservice.charge.service.BillingChargeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

// REST(BillingChargeController)와 동일하게 createCharge()를 호출하는 또 다른 입구.
// 검사서비스가 카프카로 넘긴 수납정보를 받아 처리한다.
@Slf4j
@Component
@RequiredArgsConstructor
public class BillingChargeKafkaConsumer {

    private final BillingChargeService billingChargeService;

    @KafkaListener(
            topics = "${billing.charge.topic.exam}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeExamCharge(BillingChargeRequestDTO request) {
        log.info("검사서비스 수납정보 수신: patientId={}, sourceServiceCode={}, feeCode={}",
                request.getPatientId(), request.getSourceServiceCode(), request.getFeeCode());

        billingChargeService.createCharge(request);
    }
}
