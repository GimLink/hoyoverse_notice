package com.link.hoyoversenotice.hoyolab;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * HoYoLAB API 호출 전용 RestClient.
 *
 * 모든 게임(원신/붕괴3rd/스타레일) 호출이 같은 호스트라 base-url을 박은
 * 공용 인스턴스를 둔다. 게임별 client는 이 빈을 주입받아 path와 헤더만 신경쓰면 됨.
 */
@Configuration
public class HoyolabRestClientConfig {

    @Bean
    public RestClient hoyolabRestClient(RestClient.Builder builder, HoyolabProperties properties) {
        return builder
                .baseUrl(properties.baseUrl())
                .build();
    }
}
