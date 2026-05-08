package com.link.hoyoversenotice.genshin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * PoC: 앱 시작 시 원신 데일리 노트를 한 번 호출해 응답을 콘솔에 출력한다.
 *
 * 환경변수 주입 + DS 헤더 + 쿠키 인증을 한 번에 검증하기 위한 일회용 코드.
 * 검증 끝나면 이 파일을 통째로 삭제하거나 @Profile("poc")로 격리할 것.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class GenshinDailyNotePocRunner {

    private final GenshinDailyNoteClient client;

    @Bean
    ApplicationRunner runDailyNotePoc() {
        return args -> {
            log.info("[PoC] 데일리 노트 호출 시작");
            String raw = client.fetchRaw();
            log.info("[PoC] 응답: {}", raw);
        };
    }
}
