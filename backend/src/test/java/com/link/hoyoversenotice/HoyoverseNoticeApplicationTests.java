package com.link.hoyoversenotice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * 컨텍스트 로딩 검증.
 *
 * <p>@ConfigurationProperties들이 @NotBlank로 검증되므로, 환경변수 없이도 컨텍스트가
 * 뜨도록 테스트용 더미 값을 주입한다. 실제 외부 호출이 일어나는 테스트가 아니라
 * 빈 와이어링만 확인하는 smoke test이므로 더미 값으로 충분.
 */
@SpringBootTest
@TestPropertySource(properties = {
		"hoyolab.cookie=test-cookie",
		"genshin.uid=812345678",
		"genshin.server=os_asia"
})
class HoyoverseNoticeApplicationTests {

	@Test
	void contextLoads() {
	}

}
