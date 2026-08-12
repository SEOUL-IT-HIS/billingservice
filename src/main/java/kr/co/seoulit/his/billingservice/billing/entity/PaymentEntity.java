package kr.co.seoulit.his.billingservice.billing.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="PAYMENT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PaymentEntity {

    @Id
    @Column(name="PAYMENT_ID")
    private String paymentId;

    @Column(name="BILLING_ID", nullable=false)
    private String billingId;

    @Column(name="PAYMENT_METHOD_CODE", nullable=false)
    private String paymentMethodCode;

    @Column(name="PAYMENT_AMOUNT",nullable=false)
    private Long paymentAmount;

    @Column(name="APPROVAL_NO")
    private String approvalNo;

    @Column(name="PAYMENT_STATUS",nullable=false)
    private String paymentStatus;

    @Column(name="PAYMENT_AT")
    private LocalDateTime paymentAt;

    @Column(name="CREATED_AT")
    private String createdAt;

    @Column(name="UPDATED_AT")
    private String updatedAt;
}
