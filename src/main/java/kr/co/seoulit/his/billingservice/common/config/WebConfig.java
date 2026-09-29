package kr.co.seoulit.his.billingservice.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // 새 IP/포트에서 프론트가 접속할 때마다 코드를 고치지 않도록, 허용 origin을
    // application.properties의 app.cors.allowed-origin-patterns(콤마 구분)에서 읽어온다.
    @Value("${app.cors.allowed-origin-patterns}")
    private String[] allowedOriginPatterns;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(allowedOriginPatterns)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}