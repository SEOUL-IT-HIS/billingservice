package kr.co.seoulit.his.billingservice.master.repository;

import kr.co.seoulit.his.billingservice.master.entity.BillingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillingRepository extends JpaRepository<BillingEntity, String> {

    List<BillingEntity> findByUseYn(String useYn);

    Optional<BillingEntity> findByFeeCode(String feeCode);
}
