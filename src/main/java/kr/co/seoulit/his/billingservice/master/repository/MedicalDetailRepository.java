package kr.co.seoulit.his.billingservice.master.repository;

import kr.co.seoulit.his.billingservice.master.entity.MedicalDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MedicalDetailRepository extends JpaRepository<MedicalDetail, String> {

    List<MedicalDetail> findAllByOrderByCreatedAtDesc();

    List<MedicalDetail> findByUseYnOrderByCreatedAtDesc(String useYn);

    Optional<MedicalDetail> findBySourceServiceCodeAndFeeCodeAndUseYn(
            String sourceServiceCode,
            String feeCode,
            String useYn
    );

    boolean existsBySourceServiceCodeAndFeeCode(
            String sourceServiceCode,
            String feeCode
    );

    boolean existsBySourceServiceCodeAndFeeCodeAndBillingMasterIdNot(
            String sourceServiceCode,
            String feeCode,
            String billingMasterId
    );
}
