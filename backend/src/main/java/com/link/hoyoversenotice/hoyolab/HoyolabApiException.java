package com.link.hoyoversenotice.hoyolab;

import lombok.Getter;

/**
 * HoYoLAB API가 비정상 응답을 줬을 때 던지는 예외.
 *
 * <p>호요버스 envelope의 {@code retcode != 0}이거나 응답 body 자체가 null인 경우.
 * {@link GlobalExceptionHandler}가 받아서 HTTP status로 매핑한다.
 *
 * <p>모든 게임이 같은 envelope을 공유하므로 이 예외 하나로 충분 — 게임별 예외 클래스
 * 만들 이유 없음. retcode 카탈로그가 폭발적으로 늘면 그때 분리 고려.
 */
@Getter
public class HoyolabApiException extends RuntimeException {

    private final int retcode;

    public HoyolabApiException(int retcode, String message) {
        super("HoYoLAB API 실패: retcode=" + retcode + ", message=" + message);
        this.retcode = retcode;
    }
}
