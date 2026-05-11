package com.link.hoyoversenotice.web;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 인프라 설정 — 지금은 CORS 매핑만.
 *
 * <p>{@link WebMvcConfigurer}를 구현해 {@code addCorsMappings}에서 허용 origin/메서드/경로를
 * 등록. {@link CorsProperties}에서 origin 리스트를 주입받아 환경 분리.
 *
 * <p>allowCredentials를 켜지 않음 — 프론트-백엔드 사이에 쿠키나 인증 헤더를 안 보낸다.
 * 호요버스 쿠키는 백엔드 내부에서만 쓰임. 추후 세션 기반 인증 도입하면 그때 켜면 됨.
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final CorsProperties corsProperties;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(corsProperties.allowedOrigins().toArray(String[]::new))
                .allowedMethods("GET")
                .allowCredentials(false);
    }
}
