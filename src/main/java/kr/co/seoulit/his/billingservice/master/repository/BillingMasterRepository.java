package kr.co.seoulit.his.billingservice.master.repository;

import kr.co.seoulit.his.billingservice.master.entity.BillingMasterEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BillingMasterRepository extends JpaRepository<BillingMasterEntity, String> {

    List<BillingMasterEntity> findByUseYn(String useYn);

    Optional<BillingMasterEntity> findByFeeCode(String feeCode);
}
