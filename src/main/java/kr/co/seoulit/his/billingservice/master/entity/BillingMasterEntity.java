package kr.co.seoulit.his.billingservice.master.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "BILLING_MASTER")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingEntity {

    @Id
    @Column(name = "BILLING_MASTER_ID", length = 50, nullable = false)
    private String billingMasterId;

    @Column(name = "SOURCE_SERVICE_CODE", length = 30, nullable = false)
    private String sourceServiceCode;

    @Column(name = "FEE_CODE", length = 50, nullable = false)
    private String feeCode;

    @Column(name = "FEE_NAME", length = 200, nullable = false)
    private String feeName;

    @Column(name = "DEFAULT_PRICE", precision = 18, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal defaultPrice = BigDecimal.ZERO;

    @Column(name = "CATEGORY_CODE", length = 30, nullable = false)
    private String categoryCode;

    @Column(name = "INSURANCE_TYPE_CODE", length = 30, nullable = false)
    private String insuranceTypeCode;

    @Column(name = "EFFECTIVE_FROM", nullable = false)
    private LocalDateTime effectiveFrom;

    @Column(name = "EFFECTIVE_TO", nullable = false)
    private LocalDateTime effectiveTo;

    @Column(name = "USE_YN", length = 1, nullable = false)
    @Builder.Default
    private String useYn = "Y";

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (useYn == null) useYn = "Y";
        if (defaultPrice == null) defaultPrice = BigDecimal.ZERO;
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.useYn = "N";
    }
}
