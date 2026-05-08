package com.link.hoyoversenotice.genshin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * 조회 대상이 될 원신 계정 정보.
 *
 * <p>PoC 단계라 단일 계정만 환경변수로 주입. 다중 사용자 단계로 가면
 * 이 record는 사라지고 도메인 모델 + DB로 대체될 것.
 */
@Validated
@ConfigurationProperties(prefix = "genshin")
public record GenshinAccountProperties(
        @NotBlank @Pattern(regexp = "\\d{9}", message = "원신 UID는 9자리 숫자") String uid,
        @NotBlank @Pattern(regexp = "os_(asia|euro|usa|cht)", message = "OS 서버 코드 형식") String server
) {
}
