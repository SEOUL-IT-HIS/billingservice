package kr.co.seoulit.his.billingservice.kakaopay.repository;

import jakarta.persistence.LockModeType;
import kr.co.seoulit.his.billingservice.kakaopay.entity.KakaoPayReadyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KakaoPayReadyRepository extends JpaRepository<KakaoPayReadyEntity, String> {

    // approve()가 거의 동시에 두 번 들어와도(예: 콜백 페이지 새로고침) 같은 tid로 카카오페이
    // approve API를 중복 호출하지 않도록, row를 조회하면서 트랜잭션이 끝날 때까지 잠근다.
    // 두 번째 요청은 첫 번째 트랜잭션(조회+삭제)이 끝날 때까지 여기서 대기했다가,
    // 이미 삭제된 뒤라 빈 Optional을 받아 KAKAOPAY_TID_NOT_FOUND로 끝난다.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select k from KakaoPayReadyEntity k where k.billingId = :billingId")
    Optional<KakaoPayReadyEntity> findByIdForUpdate(@Param("billingId") String billingId);
}
