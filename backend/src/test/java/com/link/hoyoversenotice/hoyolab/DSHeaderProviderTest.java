package com.link.hoyoversenotice.hoyolab;

import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DSHeaderProviderTest {

    @Test
    void compose_outputs_timestamp_random_md5() {
        String result = DSHeaderProvider.compose("mysalt", 1000L, "abcdef");

        String[] parts = result.split(",", -1);
        assertEquals(3, parts.length);
        assertEquals("1000", parts[0]);
        assertEquals("abcdef", parts[1]);
        assertTrue(parts[2].matches("[0-9a-f]{32}"), "md5 hex 32자가 와야 함: " + parts[2]);
    }

    @Test
    void compose_isDeterministic() {
        String a = DSHeaderProvider.compose("salt", 1L, "abc");
        String b = DSHeaderProvider.compose("salt", 1L, "abc");
        assertEquals(a, b);
    }

    @Test
    void compose_differentSaltYieldsDifferentHash() {
        String a = DSHeaderProvider.compose("salt-A", 1L, "abc");
        String b = DSHeaderProvider.compose("salt-B", 1L, "abc");
        assertNotEquals(a, b);
    }

    @Test
    void generate_usesInjectedClockAndRandom() {
        // 시계와 랜덤을 고정해 결과가 결정적인지 확인.
        Clock fixed = Clock.fixed(Instant.ofEpochSecond(1_700_000_000L), ZoneOffset.UTC);
        SecureRandom seeded = new SecureRandom() {
            @Override
            public int nextInt(int bound) {
                return 0;   // 항상 charset[0] = 'a'
            }
        };
        DSHeaderProvider provider = new DSHeaderProvider(fixed, seeded);

        String ds = provider.generate("test-salt");
        assertTrue(ds.startsWith("1700000000,aaaaaa,"), "고정 시계+0으로 채운 random 기대: " + ds);
    }
}
