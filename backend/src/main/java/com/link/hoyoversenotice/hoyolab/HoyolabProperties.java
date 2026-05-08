package com.link.hoyoversenotice.hoyolab;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * HoYoLAB API 통신을 위한 정적 설정.
 *
 * <p>{@code application.yml}의 {@code hoyolab.*} 키와 1:1 매핑. record라서 불변(immutable)이고
 * Spring Boot 3+ 부터 기본 지원되는 {@code @ConstructorBinding} 패턴으로 자동 바인딩됨.
 *
 * <p>사용자별 비밀(쿠키 등)도 PoC 단계라 여기 잠시 둠. 다중 사용자 진입 시
 * DB(암호화) 또는 별도 SecretsProperties로 분리 예정.
 */
@Validated
@ConfigurationProperties(prefix = "hoyolab")
public record HoyolabProperties(
        @NotBlank String baseUrl,
        @NotBlank String salt,
        @NotBlank String appVersion,
        @NotBlank String cookie
) {
}
