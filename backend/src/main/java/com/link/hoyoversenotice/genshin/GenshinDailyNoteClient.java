package com.link.hoyoversenotice.genshin;

import com.link.hoyoversenotice.hoyolab.DSHeaderProvider;
import com.link.hoyoversenotice.hoyolab.HoyolabProperties;
import com.link.hoyoversenotice.hoyolab.HoyolabResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 원신 데일리 노트 (레진/원정대/주간 보스 등) 조회 client.
 *
 * <p>호요버스 envelope({@link HoyolabResponse})을 풀어서 retcode를 검증한 뒤
 * 알맹이 {@link GenshinDailyNoteData}만 반환한다. retcode != 0 이면
 * IllegalStateException — 쿠키 만료, 잘못된 UID, 솔트 변경 등이 원인.
 */
@Component
@RequiredArgsConstructor
public class GenshinDailyNoteClient {

    private static final String PATH = "/game_record/genshin/api/dailyNote";

    // ParameterizedTypeReference는 익명 서브클래스로 두어야 제네릭 타입 정보를 런타임까지 보존.
    private static final ParameterizedTypeReference<HoyolabResponse<GenshinDailyNoteData>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final RestClient hoyolabRestClient;
    private final HoyolabProperties hoyolabProps;
    private final GenshinAccountProperties accountProps;
    private final DSHeaderProvider dsHeaderProvider;

    public GenshinDailyNoteData fetch() {
        HoyolabResponse<GenshinDailyNoteData> response = hoyolabRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(PATH)
                        .queryParam("server", accountProps.server())
                        .queryParam("role_id", accountProps.uid())
                        .build())
                .header("Cookie", hoyolabProps.cookie())
                .header("DS", dsHeaderProvider.generate(hoyolabProps.salt()))
                .header("x-rpc-app_version", hoyolabProps.appVersion())
                .header("x-rpc-client_type", "5")
                .header("x-rpc-language", "ko-kr")
                .retrieve()
                .body(RESPONSE_TYPE);

        if (response == null || !response.isSuccess()) {
            throw new IllegalStateException(
                    "HoYoLAB daily-note 호출 실패: retcode="
                            + (response == null ? "null" : response.retcode())
                            + ", message="
                            + (response == null ? "null" : response.message()));
        }
        return response.data();
    }
}
