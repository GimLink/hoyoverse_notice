package com.link.hoyoversenotice.hoyolab;

/**
 * HoYoLAB API의 공통 응답 envelope.
 *
 * <p>모든 게임 호출이 같은 형태로 감싸져 옴 — {@code {retcode, message, data}}.
 * {@code retcode}가 0이 아니면 호출 실패(쿠키 만료, 잘못된 UID 등). 호출 측에서
 * 검증 후 {@code data} 알맹이만 꺼내 반환한다.
 */
public record HoyolabResponse<T>(int retcode, String message, T data) {

    public boolean isSuccess() {
        return retcode == 0;
    }
}
