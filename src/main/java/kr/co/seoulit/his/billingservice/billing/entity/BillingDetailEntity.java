package kr.co.seoulit.his.billingservice.billing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "BILLING_DETAIL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillingDetailEntity {

    @Id
    @Column(name = "billing_detail_id")
    private String billingDetailId;

    @Column(name = "billing_id")
    private String billingId;

    @Column(name = "billing_master_id")
    private String billingMasterId;

    @Column(name = "source_service_code")
    private String sourceServiceCode;

    @Column(name = "source_record_id")
    private String sourceRecordId;

    private Integer quantity;

    private Long amount;

    @Column(name = "detail_status")
    private String detailStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
