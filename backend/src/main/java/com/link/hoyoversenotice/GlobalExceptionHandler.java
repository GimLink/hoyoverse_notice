package com.link.hoyoversenotice;

import com.link.hoyoversenotice.hoyolab.HoyolabApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

/**
 * 전역 예외 → HTTP 응답 매핑.
 *
 * <p>응답 포맷은 RFC 7807 ({@link ProblemDetail}). Spring 6 네이티브 — 자동으로
 * {@code Content-Type: application/problem+json}으로 직렬화되고 HTTP status도
 * {@code status} 필드와 자동 일치.
 *
 * <p>retcode → status 매핑은 일단 두 분기로 단순하게:
 * <ul>
 *   <li>{@code 10001} (쿠키 만료/잘못됨) → 401 Unauthorized — 프론트가 재인증 UI를 띄울 수 있도록.</li>
 *   <li>그 외 → 502 Bad Gateway — 외부 의존성(호요버스) 문제임을 명시.</li>
 * </ul>
 * 새로운 retcode를 자주 만나면 그때 분기 추가.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final int RETCODE_AUTH_FAILED = 10001;

    /**
     * 호요버스 envelope이 retcode != 0으로 답한 경우.
     */
    @ExceptionHandler(HoyolabApiException.class)
    public ProblemDetail handleHoyolabApi(HoyolabApiException ex) {
        log.warn("HoYoLAB API 실패: retcode={}", ex.getRetcode());

        HttpStatus status;
        String detail;
        if (ex.getRetcode() == RETCODE_AUTH_FAILED) {
            status = HttpStatus.UNAUTHORIZED;
            detail = "HoYoLAB 쿠키가 만료되었거나 유효하지 않습니다. 쿠키를 갱신해주세요.";
        } else {
            status = HttpStatus.BAD_GATEWAY;
            detail = "HoYoLAB API가 비정상 응답을 반환했습니다.";
        }

        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail);
        pd.setProperty("retcode", ex.getRetcode());
        return pd;
    }

    /**
     * 호요버스 서버가 envelope 자체를 안 주는 경우 — HTTP 4xx/5xx 응답이나 네트워크 실패.
     * 모두 외부 의존성 문제이므로 502로 매핑.
     */
    @ExceptionHandler(RestClientException.class)
    public ProblemDetail handleRestClient(RestClientException ex) {
        log.warn("HoYoLAB 통신 오류: {}", ex.getMessage());
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY,
                "HoYoLAB 서버와 통신할 수 없습니다.");
    }
}
