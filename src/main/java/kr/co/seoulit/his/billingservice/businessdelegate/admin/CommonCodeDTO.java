package kr.co.seoulit.his.billingservice.businessdelegate.admin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// admin-service 공통코드 항목 응답 (GET /api/admin/commonCodeItem/list?groupId=)
// 수납은 codeId/groupId/useYn으로 "등록된 코드인지"만 검증하므로 나머지 필드는 받기만 한다
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CommonCodeDTO {
    private String codeId;
    private String groupId;
    private String codeValue;
    private String codeName;
    private String useYn;
}
