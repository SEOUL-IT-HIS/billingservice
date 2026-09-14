package kr.co.seoulit.his.billingservice.charge.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

// 이 프로젝트의 스프링부트 버전에서는 spring-kafka 자동설정이 컨슈머 팩토리/@EnableKafka를
// 자동으로 등록해주지 않아서, BillingChargeKafkaConsumer(@KafkaListener)가 동작하려면
// 직접 빈으로 정의해야 한다.
@EnableKafka
@Configuration
@ConditionalOnProperty(prefix = "app.kafka", name = "enabled", havingValue = "true", matchIfMissing = true)
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "kr.co.seoulit.his.billingservice.charge.dto");
        // 검사서비스는 __TypeId__ 헤더 없이 순수 JSON만 발행하기로 함 - 헤더를 보는 대신
        // 리스너별로 지정한 spring.json.value.default.type(BillingChargeRequestDTO)로 고정 역직렬화한다.
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        // 타 서비스가 우리 DTO에 없는 필드를 같이 보내도 역직렬화가 깨지지 않도록
        // "모르는 필드 무시" 옵션을 켠 ObjectMapper를 직접 구성한다.
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(objectMapper);
        ErrorHandlingDeserializer<Object> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), errorHandlingDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // 검사서비스는 발행 실패 시 재시도 없이 로그만 남기기로 함 - 우리 쪽도 별도 DLQ 인프라가
        // 없으므로, 처리 실패 시 짧게 2회만 재시도하고 그래도 안 되면 로그만 남기고 다음 메시지로
        // 넘어간다(오프셋 커밋). 파티션이 통째로 막히는 것을 방지하기 위함.
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(1000L, 2)));
        return factory;
    }
}
