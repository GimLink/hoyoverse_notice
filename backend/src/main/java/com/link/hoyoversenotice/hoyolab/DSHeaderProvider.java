package com.link.hoyoversenotice.hoyolab;

import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;

/**
 * HoYoLAB 비공식 API의 DS(Dynamic Secret) 헤더 생성기.
 *
 * 형식: {@code "{timestamp},{random6},{md5(salt=...&t=...&r=...)}"}
 *
 * salt는 이 클래스가 보유하지 않는다. 엔드포인트별로 다를 수 있어 호출 측이 주입한다.
 */
@Component
public class DSHeaderProvider {

    private static final String CHARSET = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_LEN = 6;

    private final Clock clock;
    private final SecureRandom random;

    public DSHeaderProvider() {
        this(Clock.systemUTC(), new SecureRandom());
    }

    DSHeaderProvider(Clock clock, SecureRandom random) {
        this.clock = clock;
        this.random = random;
    }

    public String generate(String salt) {
        long timestamp = Instant.now(clock).getEpochSecond();
        String randomStr = randomString();
        return compose(salt, timestamp, randomStr);
    }

    static String compose(String salt, long timestamp, String randomStr) {
        String input = "salt=" + salt + "&t=" + timestamp + "&r=" + randomStr;
        String hash = DigestUtils.md5DigestAsHex(input.getBytes(StandardCharsets.UTF_8));
        return timestamp + "," + randomStr + "," + hash;
    }

    private String randomString() {
        StringBuilder sb = new StringBuilder(RANDOM_LEN);
        for (int i = 0; i < RANDOM_LEN; i++) {
            sb.append(CHARSET.charAt(random.nextInt(CHARSET.length())));
        }
        return sb.toString();
    }
}
