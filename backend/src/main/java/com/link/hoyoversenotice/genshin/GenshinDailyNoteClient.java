package com.link.hoyoversenotice.genshin;

import com.link.hoyoversenotice.hoyolab.HoyolabApiCaller;
import com.link.hoyoversenotice.hoyolab.HoyolabResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 원신 데일리 노트 (레진/원정대/주간 보스 등) 조회 client.
 *
 * <p>플랫폼 공통 호출 로직은 {@link HoyolabApiCaller}에 위임. 이 client는
 * 원신-특화 path, query, 응답 타입만 안다.
 */
@Component
@RequiredArgsConstructor
public class GenshinDailyNoteClient {

    private static final String PATH = "/game_record/genshin/api/dailyNote";

    private static final ParameterizedTypeReference<HoyolabResponse<GenshinDailyNoteData>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private final HoyolabApiCaller hoyolab;
    private final GenshinAccountProperties accountProps;

    public GenshinDailyNoteData fetch() {
        return hoyolab.get(
                PATH,
                Map.of(
                        "server", accountProps.server(),
                        "role_id", accountProps.uid()),
                RESPONSE_TYPE);
    }
}
