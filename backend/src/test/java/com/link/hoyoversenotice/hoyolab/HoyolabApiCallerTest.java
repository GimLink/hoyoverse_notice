package com.link.hoyoversenotice.hoyolab;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * HoyolabApiCaller 단위 테스트.
 *
 * <p>MockRestServiceServer로 RestClient의 HTTP 전송을 가로채서 호요버스 응답을
 * 흉내낸다. 실제 네트워크는 안 나가지만 RestClient + Jackson 직렬화/역직렬화는 진짜로 작동.
 */
class HoyolabApiCallerTest {

    /**
     * 테스트 전용 미니 DTO. envelope의 data 필드에 매핑되는 record.
     * 진짜 GenshinDailyNoteData 끌어오는 대신 hoyolab 단위 테스트에서 자급자족.
     */
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    record TestData(int currentResin) {}

    private static final ParameterizedTypeReference<HoyolabResponse<TestData>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {};

    private MockRestServiceServer server;
    private HoyolabApiCaller caller;

    @BeforeEach
    void setup() {
        // 1. RestClient.Builder 하나 만들어서 그 위에 MockRestServiceServer를 묶음.
        //    이 builder로 build한 RestClient의 HTTP 전송은 모두 server가 가로챔.
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = builder.build();

        // 2. 의존성들은 진짜 객체로 — 호요버스 어드레스/쿠키는 더미.
        HoyolabProperties props = new HoyolabProperties(
                "https://test-host", "test-salt", "1.5.0", "test-cookie");
        DSHeaderProvider ds = new DSHeaderProvider();

        caller = new HoyolabApiCaller(restClient, props, ds);
    }

    @Test
    void retcode_0이면_data_알맹이_반환() {
        // given: 호요버스가 정상 응답을 줄 것이라고 server에 등록.
        server.expect(requestTo("/some/path?role_id=12345"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(
                        """
                        {"retcode": 0, "message": "OK", "data": {"current_resin": 100}}
                        """,
                        MediaType.APPLICATION_JSON));

        // when: caller.get 호출 — 실제로는 mock server가 받아서 위 응답 줌.
        TestData data = caller.get("/some/path", Map.of("role_id", "12345"), RESPONSE_TYPE);

        // then: envelope 풀려서 data 알맹이만 우리에게 옴.
        assertThat(data.currentResin()).isEqualTo(100);
    }

    @Test
    void 호요버스가_HTTP_5xx_주면_RestClientException_던짐() {
        // given: 호요버스 서버가 500을 돌려준다 (envelope 자체 없음).
        server.expect(requestTo("/some/path"))
                .andRespond(withServerError());

        // when / then: RestClient가 5xx를 RestClientException으로 변환해 던짐.
        // 우리 caller는 잡지 않고 흘려보냄 — GlobalExceptionHandler가 502로 매핑할 책임.
        assertThatThrownBy(() -> caller.get("/some/path", Map.of(), RESPONSE_TYPE))
                .isInstanceOf(RestClientException.class);
    }

    @Test
    void retcode_10001이면_HoyolabApiException_던지고_retcode_보존() {
        // given: 호요버스가 쿠키 만료(10001) 응답을 준다.
        server.expect(requestTo("/some/path"))
                .andRespond(withSuccess(
                        """
                        {"retcode": 10001, "message": "Please login", "data": null}
                        """,
                        MediaType.APPLICATION_JSON));

        // when / then: HoyolabApiException이 던져지고, retcode가 10001로 보존돼야 한다.
        // 이게 핵심 — 핸들러에서 retcode로 분기하니까 잘 전달돼야 함.
        assertThatThrownBy(() -> caller.get("/some/path", Map.of(), RESPONSE_TYPE))
                .isInstanceOf(HoyolabApiException.class)
                .hasFieldOrPropertyWithValue("retcode", 10001)
                .hasMessageContaining("Please login");
    }
}
