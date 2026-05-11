package com.link.hoyoversenotice.genshin;

import com.link.hoyoversenotice.GlobalExceptionHandler;
import com.link.hoyoversenotice.hoyolab.HoyolabApiException;
import org.springframework.web.client.ResourceAccessException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 컨트롤러 슬라이스 테스트.
 *
 * <p>{@link GenshinDailyNoteController} 한 개와 {@link GlobalExceptionHandler}만 로드.
 * 컨트롤러의 의존성({@link GenshinDailyNoteClient})은 @MockitoBean으로 대체해서
 * 다양한 시나리오를 트리거한다.
 */
@WebMvcTest(GenshinDailyNoteController.class)
@Import(GlobalExceptionHandler.class)   // 슬라이스가 자동 포함하지만 명시적으로 — 의도 표현
class GenshinDailyNoteControllerTest {

    @Autowired
    MockMvc mvc;

    @MockitoBean
    GenshinDailyNoteClient client;

    @Test
    void 정상_data_받으면_200_그리고_snake_case로_직렬화() throws Exception {
        // given: client가 정상 data를 돌려준다고 가정.
        GenshinDailyNoteData fixture = sampleData(100);
        when(client.fetch()).thenReturn(fixture);

        // when / then: 200 OK + body는 snake_case JSON.
        mvc.perform(get("/api/genshin/daily-note"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.current_resin").value(100))
                .andExpect(jsonPath("$.max_resin").value(200));
    }

    @Test
    void retcode_10001_예외면_401_ProblemDetail() throws Exception {
        // given: client가 쿠키 만료 예외를 던진다.
        when(client.fetch()).thenThrow(new HoyolabApiException(10001, "Please login"));

        // when / then: 401 + application/problem+json + retcode 노출.
        mvc.perform(get("/api/genshin/daily-note"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.detail", containsString("쿠키")))
                .andExpect(jsonPath("$.instance").value("/api/genshin/daily-note"))
                .andExpect(jsonPath("$.retcode").value(10001));
    }

    @Test
    void 다른_retcode_예외면_502_ProblemDetail() throws Exception {
        // given: 10001 이외의 retcode (rate limit 등) — 호요버스 측 일반 오류로 간주.
        when(client.fetch()).thenThrow(new HoyolabApiException(10101, "rate limit"));

        // when / then: 502 Bad Gateway.
        mvc.perform(get("/api/genshin/daily-note"))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.retcode").value(10101));
    }

    @Test
    void 네트워크_장애면_502_ProblemDetail() throws Exception {
        // given: 호요버스 서버에 닿지 못함 (RestClient가 ResourceAccessException으로 변환).
        // ResourceAccessException은 RestClientException의 서브타입 — handler가 부모로 잡음.
        when(client.fetch()).thenThrow(new ResourceAccessException("connection refused"));

        // when / then: envelope 없음, retcode 필드도 없음. 단순 502.
        mvc.perform(get("/api/genshin/daily-note"))
                .andExpect(status().isBadGateway())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.status").value(502))
                .andExpect(jsonPath("$.retcode").doesNotExist());
    }

    private static GenshinDailyNoteData sampleData(int currentResin) {
        return new GenshinDailyNoteData(
                currentResin, 200, "0",
                4, 4, true,
                3, 3,
                5, 5, List.of(),
                2400, 2400, "0", "",
                Map.of(), null, Map.of(), Map.of());
    }
}
