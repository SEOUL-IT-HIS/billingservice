package kr.co.seoulit.his.billingservice.billing.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 입원 건 결제(퇴원정산) 완료 이벤트.
// 스프링 내부 이벤트(ApplicationEventPublisher)로 먼저 던지고, 커밋이 끝난 뒤 그대로 카프카 payload로 발행한다.
// 병동서비스와 합의한 메시지 모양: { "admissionId": "A0xx" }
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettlementCompletedEvent {

    private String admissionId;
}
