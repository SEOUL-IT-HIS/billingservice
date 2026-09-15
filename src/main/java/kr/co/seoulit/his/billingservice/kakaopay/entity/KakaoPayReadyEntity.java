package kr.co.seoulit.his.billingservice.kakaopay.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// ready~approve 사이에만 필요한 tid를 저장. 서버 재시작/다중 인스턴스에서도
// 잃어버리지 않도록 메모리(tidStore) 대신 DB로 관리한다.
@Entity
@Table(name = "KAKAOPAY_READY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KakaoPayReadyEntity {

    @Id
    @Column(name = "BILLING_ID", length = 50)
    private String billingId;

    @Column(name = "TID", length = 50, nullable = false)
    private String tid;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;
}
