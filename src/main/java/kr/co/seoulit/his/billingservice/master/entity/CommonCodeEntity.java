package kr.co.seoulit.his.billingservice.master.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

// ADMIN 계정 소유 공통코드 테이블. billing 계정엔 SELECT 권한만 부여돼 있어 조회 전용으로만 쓴다.
@Entity
@Table(name = "COMMON_CODE", schema = "ADMIN")
@Getter
@NoArgsConstructor
public class CommonCodeEntity {

    @Id
    @Column(name = "CODE_ID", length = 36)
    private String codeId;

    @Column(name = "GROUP_ID", length = 36)
    private String groupId;

    @Column(name = "USE_YN", length = 1)
    private String useYn;
}
