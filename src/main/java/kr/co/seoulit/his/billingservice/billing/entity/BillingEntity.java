package kr.co.seoulit.his.billingservice.billing.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "BILLING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class BillingEntity {

    @Id
    @Column(name = "billing_id")
    private String billingId;

    @Column(name = "patient_id")
    private String patientId;

    @Column(name = "visit_id")
    private String visitId;

    @Column(name = "admission_id")
    private String admissionId;

    @Column(name = "billing_status")
    private String billingStatus;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}