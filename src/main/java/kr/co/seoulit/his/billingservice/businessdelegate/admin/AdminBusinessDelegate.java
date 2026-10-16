package kr.co.seoulit.his.billingservice.businessdelegate.admin;

import kr.co.seoulit.his.billingservice.common.exception.BusinessException;
import kr.co.seoulit.his.billingservice.common.exception.ErrorCode;
import kr.co.seoulit.his.billingservice.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

// 개발표준가이드 '타 서비스 DB 직접 접근 금지'에 따라 ADMIN.COMMON_CODE를 직접 조회하지 않고
// admin-service 공통코드 API로 조회한다. 공통코드는 거의 안 바뀌는데 수가 등록/Kafka 청구마다
// 매번 API를 부르면 admin에 부하가 가므로, 그룹별 "사용중(useYn=Y) codeId 목록"을 잠깐 캐시해둔다.
@Slf4j
@Component
public class AdminBusinessDelegate {

    private final RestTemplate restTemplate;
    private final String adminServiceBaseUrl;
    private final Duration cacheTtl;

    // groupId -> (사용중 codeId 목록, 만료시각)
    private final Map<String, CachedCodes> cache = new ConcurrentHashMap<>();

    public AdminBusinessDelegate(
            RestTemplate restTemplate,
            @Value("${admin.service.base-url}") String adminServiceBaseUrl,
            @Value("${admin.common-code.cache-ttl-seconds:300}") long cacheTtlSeconds
    ) {
        this.restTemplate = restTemplate;
        this.adminServiceBaseUrl = adminServiceBaseUrl;
        this.cacheTtl = Duration.ofSeconds(cacheTtlSeconds);
    }

    // 기존 commonCodeRepository.existsByCodeIdAndGroupIdAndUseYn(codeId, groupId, "Y")와 같은 의미
    public boolean isActiveCode(String codeId, String groupId) {
        if (codeId == null || groupId == null) {
            return false;
        }
        return getActiveCodeIds(groupId).contains(codeId);
    }

    private Set<String> getActiveCodeIds(String groupId) {
        CachedCodes cached = cache.get(groupId);
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) {
            return cached.codeIds();
        }
        Set<String> codeIds = fetchActiveCodeIds(groupId);
        cache.put(groupId, new CachedCodes(codeIds, Instant.now().plus(cacheTtl)));
        return codeIds;
    }

    // admin은 {code, message, data} 봉투로 응답하므로 ApiResponse<List<CommonCodeDTO>>로 언래핑 후 data만 꺼낸다
    private Set<String> fetchActiveCodeIds(String groupId) {
        try {
            ResponseEntity<ApiResponse<List<CommonCodeDTO>>> response = restTemplate.exchange(
                    adminServiceBaseUrl + "/api/admin/commonCodeItem/list?groupId={groupId}",
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<List<CommonCodeDTO>>>() {},
                    groupId
            );
            ApiResponse<List<CommonCodeDTO>> body = response.getBody();
            if (body == null || body.getData() == null) {
                return Set.of();
            }
            return body.getData().stream()
                    .filter(code -> "Y".equals(code.getUseYn()))
                    .map(CommonCodeDTO::getCodeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toUnmodifiableSet());
        } catch (RestClientException e) {
            log.error("admin 공통코드 조회 실패 (groupId={}): {}", groupId, e.getMessage(), e);
            throw new BusinessException(ErrorCode.ADMIN_SERVICE_UNAVAILABLE);
        }
    }

    private record CachedCodes(Set<String> codeIds, Instant expiresAt) {}
}
