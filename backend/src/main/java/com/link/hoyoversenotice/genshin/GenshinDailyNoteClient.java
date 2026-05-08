package com.link.hoyoversenotice.genshin;

import com.link.hoyoversenotice.hoyolab.DSHeaderProvider;
import com.link.hoyoversenotice.hoyolab.HoyolabProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * 원신 데일리 노트 (레진/원정대/주간 보스 등) 조회 client.
 *
 * PoC 단계라 응답을 String 그대로 리턴한다. DTO 매핑은 응답 구조 확인 후 다음 단계.
 */
@Component
@RequiredArgsConstructor
public class GenshinDailyNoteClient {

    private static final String PATH = "/game_record/genshin/api/dailyNote";

    private final RestClient hoyolabRestClient;
    private final HoyolabProperties hoyolabProps;
    private final GenshinAccountProperties accountProps;
    private final DSHeaderProvider dsHeaderProvider;

    public String fetchRaw() {
        return hoyolabRestClient.get()
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
                .body(String.class);
    }
}
