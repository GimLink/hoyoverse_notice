package com.link.hoyoversenotice.genshin;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.util.List;
import java.util.Map;

/**
 * HoYoLAB daily-note 응답의 {@code data} 부분.
 *
 * <p>외부 응답 구조를 그대로 유지한다 — envelope({@code retcode}, {@code message})만
 * 벗기고, {@code data} 알맹이는 우리 API 응답으로도 그대로 흘려보낸다.
 *
 * <p>대시보드에서 직접 사용하는 부분({@code expeditions}, {@code daily_task})은
 * 중첩 record로 풀어두고, 지금 안 쓰는 객체({@code transformer},
 * {@code archon_quest_progress}, {@code week_active_progress})는 {@code Map}으로
 * 받아 통과시킨다. 나중에 필요해지면 그때 record로 채울 것 (rule of three).
 *
 * <p>{@link JsonNaming}으로 snake_case 자동 매핑. 자바 필드는 camelCase, JSON 와이어는
 * snake_case로 양방향 변환됨. 중첩 record에도 동일한 어노테이션을 붙여야 cascade 됨.
 */
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GenshinDailyNoteData(
        int currentResin,
        int maxResin,
        String resinRecoveryTime,
        int finishedTaskNum,
        int totalTaskNum,
        boolean isExtraTaskRewardReceived,
        int remainResinDiscountNum,
        int resinDiscountNumLimit,
        int currentExpeditionNum,
        int maxExpeditionNum,
        List<Expedition> expeditions,
        int currentHomeCoin,
        int maxHomeCoin,
        String homeCoinRecoveryTime,
        String calendarUrl,
        Map<String, Object> transformer,
        DailyTask dailyTask,
        Map<String, Object> archonQuestProgress,
        Map<String, Object> weekActiveProgress
) {

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record Expedition(
            String avatarSideIcon,
            String status,
            String remainedTime
    ) {}

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public record DailyTask(
            int totalNum,
            int finishedNum,
            boolean isExtraTaskRewardReceived,
            List<TaskReward> taskRewards,
            List<AttendanceReward> attendanceRewards,
            boolean attendanceVisible,
            String storedAttendance,
            long storedAttendanceRefreshCountdown
    ) {

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record TaskReward(String status) {}

        @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
        public record AttendanceReward(String status, int progress) {}
    }
}
