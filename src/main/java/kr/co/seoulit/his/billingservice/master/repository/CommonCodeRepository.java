package kr.co.seoulit.his.billingservice.master.repository;

import kr.co.seoulit.his.billingservice.master.entity.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, String> {

    boolean existsByCodeIdAndGroupIdAndUseYn(String codeId, String groupId, String useYn);
}
