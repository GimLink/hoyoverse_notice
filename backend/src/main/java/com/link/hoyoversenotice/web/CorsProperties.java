package com.link.hoyoversenotice.web;

import jakarta.validation.constraints.NotEmpty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 브라우저 same-origin policy 우회를 위한 CORS 허용 origin 설정.
 *
 * <p>{@code application.yml}의 {@code cors.*}와 매핑. 환경별로 다른 origin이 필요할 수 있어
 * (로컬 dev: localhost:5173, prod: GitHub Pages) 외부화.
 *
 * <p>{@code @NotEmpty}: CORS 설정이 빈 리스트면 그 자체로 의미 없는 상태이므로 부팅 시점에
 * 막음.
 */
@Validated
@ConfigurationProperties(prefix = "cors")
public record CorsProperties(
        @NotEmpty List<String> allowedOrigins
) {
}
