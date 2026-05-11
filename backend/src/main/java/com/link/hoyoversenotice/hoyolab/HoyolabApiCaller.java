package com.link.hoyoversenotice.hoyolab;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * 호요버스 게임 record API의 공통 호출 로직.
 *
 * <p>모든 게임이 같은 호스트, 같은 헤더 셋, 같은 envelope 형식을 쓰므로 그 부분을
 * 이 빈에 모은다. 게임별 client는 path, query, 응답 타입 세 가지만 신경쓰면 됨.
 *
 * <p>retcode 검증 결과 비정상이면 {@link HoyolabApiException}을 던진다 —
 * HTTP status 매핑은 {@code GlobalExceptionHandler}가 담당.
 */
@Component
@RequiredArgsConstructor
public class HoyolabApiCaller {

    private final RestClient hoyolabRestClient;
    private final HoyolabProperties hoyolabProps;
    private final DSHeaderProvider dsHeaderProvider;

    public <T> T get(String path,
                     Map<String, ?> query,
                     ParameterizedTypeReference<HoyolabResponse<T>> responseType) {
        HoyolabResponse<T> response = hoyolabRestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(path);
                    query.forEach(uriBuilder::queryParam);
                    return uriBuilder.build();
                })
                .header("Cookie", hoyolabProps.cookie())
                .header("DS", dsHeaderProvider.generate(hoyolabProps.salt()))
                .header("x-rpc-app_version", hoyolabProps.appVersion())
                .header("x-rpc-client_type", "5")
                .header("x-rpc-language", "ko-kr")
                .retrieve()
                .body(responseType);

        if (response == null) {
            throw new HoyolabApiException(-1, "응답 body가 null");
        }
        if (!response.isSuccess()) {
            throw new HoyolabApiException(response.retcode(), response.message());
        }
        return response.data();
    }
}
