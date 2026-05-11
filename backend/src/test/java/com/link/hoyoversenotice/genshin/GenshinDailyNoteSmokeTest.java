package com.link.hoyoversenotice.genshin;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 호요버스 서버에 진짜로 호출하는 smoke test.
 *
 * <p><b>실행 방법:</b>
 * <pre>
 *   # 평소엔 RUN_REAL_API 환경변수 없어서 자동 스킵.
 *   # 진짜로 돌리려면 .env.local의 비밀과 함께 RUN_REAL_API=true 박아서 실행.
 *   RUN_REAL_API=true HOYO_COOKIE=... GENSHIN_UID=... GENSHIN_SERVER=... ./mvnw test
 * </pre>
 *
 * <p><b>왜 분리되어 있나:</b>
 * <ul>
 *   <li>네트워크 의존 — CI/오프라인에서 깨짐.</li>
 *   <li>인증 — 실 쿠키 필요, 정기 갱신 필요.</li>
 *   <li>속도 — 매 호출 200-500ms.</li>
 *   <li>상태성 — 게임 상태가 시간에 따라 변함.</li>
 * </ul>
 *
 * <p><b>무엇을 검증하나:</b> 값이 아니라 <b>구조와 invariant</b>. 호요버스가 응답 schema를
 * 바꿨거나 envelope을 바꾸면 여기서 잡힘 — mocked 테스트가 거짓말하기 시작했다는 신호.
 */
@SpringBootTest
@Tag("real-api")  // IDE 그룹핑/필터링용. 실행 제어는 아래 @EnabledIfEnvironmentVariable가 담당.
@EnabledIfEnvironmentVariable(named = "RUN_REAL_API", matches = "true")
class GenshinDailyNoteSmokeTest {

    @Autowired
    GenshinDailyNoteClient client;

    @Test
    void 진짜_호요버스_응답이_GenshinDailyNoteData_record와_호환된다() {
        GenshinDailyNoteData data = client.fetch();

        // 1. envelope 풀려서 알맹이 나옴.
        assertThat(data).isNotNull();

        // 2. resin invariant — max는 양수, current는 0~max 사이.
        assertThat(data.maxResin()).isPositive();
        assertThat(data.currentResin()).isBetween(0, data.maxResin());

        // 3. expedition 일관성 — current는 max 이하, 리스트도 max 이하 (혹은 동일).
        assertThat(data.currentExpeditionNum()).isLessThanOrEqualTo(data.maxExpeditionNum());
        assertThat(data.expeditions()).isNotNull();
        assertThat(data.expeditions().size()).isLessThanOrEqualTo(data.maxExpeditionNum());

        // 4. 데일리 진척도 invariant.
        assertThat(data.finishedTaskNum()).isBetween(0, data.totalTaskNum());
    }
}
