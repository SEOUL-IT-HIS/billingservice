package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<BillingEntity, String> {

    // 새 charge를 붙일 "아직 결제 전(READY)" billing만 찾는다.
    // 상태 조건 없이 찾으면 이미 결제 완료(SUCCESS)된 billing에 새 항목이 붙어서
    // 결제된 청구서 합계가 바뀌고, 새 항목은 수납대기 목록(READY만 조회)에 영영 안 나타난다.
    Optional<BillingEntity> findByReceptionIdAndBillingStatus(String receptionId, String billingStatus);

    Optional<BillingEntity> findByAdmissionIdAndBillingStatus(String admissionId, String billingStatus);

}
