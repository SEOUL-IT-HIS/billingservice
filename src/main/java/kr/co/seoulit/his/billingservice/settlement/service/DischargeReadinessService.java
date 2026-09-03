package kr.co.seoulit.his.billingservice.settlement.service;

import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import kr.co.seoulit.his.billingservice.billing.repository.BillingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

// 퇴원 요청 신호(discharge.requested 토픽, 또는 병동서비스가 charge 등록 토픽에 함께 보내는
// feeCode=DISCHARGE_REQUEST)를 받았을 때 공통으로 쓰는 로직. 진료비는 입원 기간 내내 charge 등록
// 시점에 이미 admissionId 기준으로 billing에 누적되고 있으므로, 여기서 별도로 집계/생성할 것은 없고
// 해당 admissionId의 billing이 실제로 존재하는지만 확인한다.
@Slf4j
@Service
@RequiredArgsConstructor
public class DischargeReadinessService {

    // charge 등록과 퇴원 요청이 거의 동시에 들어오면 서로 다른 컨슈머 스레드에서 처리되다 보니
    // 순서가 보장되지 않는다. 못 찾았다고 바로 단정하지 않고 짧게 텀을 두고 몇 번 더 확인한다.
    private static final int MAX_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MILLIS = 2000;

    private final BillingRepository billingRepository;

    public void checkDischargeReadiness(String admissionId) {
        BillingEntity billing = findBillingWithRetry(admissionId);

        if (billing == null) {
            log.warn("퇴원 요청을 받았지만 해당 admissionId의 수납 정보가 없습니다: admissionId={}", admissionId);
            return;
        }

        log.info("퇴원 요청 수신, 정산 준비 완료: admissionId={}, billingId={}, billingStatus={}",
                admissionId, billing.getBillingId(), billing.getBillingStatus());
    }

    private BillingEntity findBillingWithRetry(String admissionId) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            BillingEntity billing = billingRepository.findByAdmissionId(admissionId).orElse(null);
            if (billing != null) {
                return billing;
            }

            if (attempt < MAX_ATTEMPTS) {
                try {
                    Thread.sleep(RETRY_DELAY_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return null;
                }
            }
        }
        return null;
    }
}
