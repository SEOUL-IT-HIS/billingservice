package kr.co.seoulit.his.billingservice.billing.repository;

import kr.co.seoulit.his.billingservice.billing.dto.PayCancelIdDTO;
import kr.co.seoulit.his.billingservice.billing.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PaymentRepository extends JpaRepository<PaymentEntity, String> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE PaymentEntity p SET p.paymentStatus = 'CANCELLED', p.updatedAt = CURRENT_TIMESTAMP " +
           "WHERE p.paymentId = :paymentId AND p.paymentStatus = 'APPROVED'")
    int cancelPayment(@Param("paymentId") String paymentId);
}
