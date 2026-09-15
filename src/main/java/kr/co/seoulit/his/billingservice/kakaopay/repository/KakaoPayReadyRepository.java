package kr.co.seoulit.his.billingservice.kakaopay.repository;

import kr.co.seoulit.his.billingservice.kakaopay.entity.KakaoPayReadyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaoPayReadyRepository extends JpaRepository<KakaoPayReadyEntity, String> {
}
