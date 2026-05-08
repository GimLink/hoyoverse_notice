package com.link.hoyoversenotice.genshin;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 원신 계정 상태 조회 엔드포인트.
 *
 * <p>현재는 데일리 노트 하나뿐. 향후 캐릭터/주간 보스 등이 추가되면 같은 prefix
 * 아래로 들어옴.
 */
@RestController
@RequestMapping("/api/genshin")
@RequiredArgsConstructor
public class GenshinDailyNoteController {

    private final GenshinDailyNoteClient client;

    @GetMapping("/daily-note")
    public GenshinDailyNoteData getDailyNote() {
        return client.fetch();
    }
}