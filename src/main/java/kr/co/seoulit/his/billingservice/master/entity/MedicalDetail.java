package kr.co.seoulit.his.billingservice.master.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "billing_master")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MedicalDetail {

    @Id
    @Column(name = "billing_master_id")
    private String billingMasterId;

    @Column(name = "source_service_code")
    private String sourceServiceCode;

    @Column(name = "fee_code")
    private String feeCode;

    @Column(name = "fee_name")
    private String feeName;

    @Column(name = "default_price")
    private BigDecimal defaultPrice;

    @Column(name = "category_code")
    private String categoryCode;

    @Column(name = "insurance_type_code")
    private String insuranceTypeCode;

    @Column(name = "effective_from")
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

    @Column(name = "use_yn")
    private String useYn;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Builder(toBuilder = true)
    public MedicalDetail(
            String billingMasterId,
            String sourceServiceCode,
            String feeCode,
            String feeName,
            BigDecimal defaultPrice,
            String categoryCode,
            String insuranceTypeCode,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String useYn,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        this.billingMasterId = billingMasterId;
        this.sourceServiceCode = sourceServiceCode;
        this.feeCode = feeCode;
        this.feeName = feeName;
        this.defaultPrice = defaultPrice;
        this.categoryCode = categoryCode;
        this.insuranceTypeCode = insuranceTypeCode;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.useYn = useYn;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public void update(
            String sourceServiceCode,
            String feeCode,
            String feeName,
            BigDecimal defaultPrice,
            String categoryCode,
            String insuranceTypeCode,
            LocalDate effectiveFrom,
            LocalDate effectiveTo,
            String useYn,
            LocalDateTime updatedAt
    ) {
        this.sourceServiceCode = sourceServiceCode;
        this.feeCode = feeCode;
        this.feeName = feeName;
        this.defaultPrice = defaultPrice;
        this.categoryCode = categoryCode;
        this.insuranceTypeCode = insuranceTypeCode;
        this.effectiveFrom = effectiveFrom;
        this.effectiveTo = effectiveTo;
        this.useYn = useYn;
        this.updatedAt = updatedAt;
    }

    public void changeUseYn(String useYn, LocalDateTime updatedAt) {
        this.useYn = useYn;
        this.updatedAt = updatedAt;
    }
}
