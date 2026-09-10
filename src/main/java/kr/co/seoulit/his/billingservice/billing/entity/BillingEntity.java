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

    @Column(name = "reception_id")
    private String receptionId;

    @Column(name = "admission_id")
    private String admissionId;

    @Column(name = "billing_status")
    private String billingStatus;

    @Column(name = "total_amount")
    private String totalAmount;

    @Column(name = "insurance_amount")
    private String insuranceAmount;

    @Column(name = "patient_amount")
    private String patientAmount;
    //보험/본인부담금 분리 계산 로직이 아직 없어서 생성 시 "0"으로 채움 - 계산 로직 만들면 실제값으로 교체 필요

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}