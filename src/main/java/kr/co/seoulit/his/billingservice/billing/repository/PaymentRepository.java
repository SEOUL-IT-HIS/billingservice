package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {
}
