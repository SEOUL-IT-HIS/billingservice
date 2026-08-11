package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.entity.BillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BillingRepository extends JpaRepository<BillingEntity, String> {

    Optional<BillingEntity> findByVisitId(String visitId);

    Optional<BillingEntity> findByAdmissionId(String admissionId);

}