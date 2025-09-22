package com.airbng.consumer.service;

import com.airbng.consumer.domain.base.NotificationType;
import com.airbng.consumer.dto.AlarmPayloadResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationAlarmCacheServiceImpl implements ReservationAlarmCacheService{

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환용

    //알림이 발송된 후 24시간 동안 중복 발송 방지
    private static final long EXPIRE_SECONDS = 24 * 60 * 60;

    @Override
    public boolean tryMarkSent(Long reservationId, Long receiverId, NotificationType type) {
        String key = buildKey(reservationId, receiverId, type);
        // 처음 SET하는 경우에만 true 리턴
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", EXPIRE_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }

    //Redis 키를 일관되게 만들기 위한 헬퍼 메서드
    private String buildKey(Long reservationId, Long receiverId, NotificationType type) {
        return String.format("alarm:%d:%d:%s", reservationId, receiverId, type.name());
    }

    //안읽은 알림 표시
    public void markUnread(Long memberId) {
        String key = buildUnreadKey(memberId);
        // TTL 제거 (영구 저장 -> 사용자가 안읽으면 계속 안읽음 표시)
        redisTemplate.opsForValue().set(key, "true");
        log.info("안읽은 알림 표시: memberId={}", memberId);
    }

    //읽은 알림 표시
    public void markAllAsRead(Long memberId) {
        redisTemplate.delete(buildUnreadKey(memberId)); // 읽음 처리 (삭제)
        log.info("모든 알림 읽음 처리: memberId={}", memberId);
    }

    //안읽음 확인
    public boolean hasUnreadAlarm(Long memberId) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(buildUnreadKey(memberId)));
    }

    //Redis 키를 일관되게 만들기 위한 헬퍼 메서드
    private String buildUnreadKey(Long memberId) {
        return "alarm:unread:" + memberId;
    }

    //sse 재연결 시 놓친 알림 가져오기
    @Override
    public List<AlarmPayloadResponse> getMissedAlarms(Long memberId, String lastEventId) {
        String redisKey = buildAlarmListKey(memberId);

        List<String> all = redisTemplate.opsForList().range(redisKey, 0, -1); // 전체 리스트 조회
        if (all == null) return List.of();

        long lastId;
        if (lastEventId != null) {
            lastId = Long.parseLong(lastEventId); // String을 Long으로 변환
        } else {
            lastId = 0;
        }

        return all.stream()
                .map(s -> s.split("\\|", 2))          // [eventId, payload]
                .filter(arr -> Long.parseLong(arr[0]) > lastId) // lastEventId 이후 것만 가져옴
                .map(arr -> new AlarmPayloadResponse(Long.parseLong(arr[0]), arr[1]))    // payload만 추출
                .reduce((first, second) -> second)
                .map(List::of)
                .orElse(List.of());
    }


    //알림 저장(value값으로 쌓임) -> sse 연결 시 놓친 알림 전송용
    @Override
    public String saveAlarm(Long memberId, Object payload) {
        Long eventId = redisTemplate.opsForValue().increment("alarm:seq:" + memberId);
        String redisKey = buildAlarmListKey(memberId);
        String value;

        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            value = eventId + "|" + jsonPayload;
        } catch (Exception e) {
            throw new RuntimeException("알림 직렬화 실패", e);
        }

        redisTemplate.opsForList().rightPush(redisKey, value);
        redisTemplate.expire(redisKey, 24, TimeUnit.HOURS);
        return eventId.toString();
    }

    //회원별 알림 리스트 키
    private String buildAlarmListKey(Long memberId) {
        return "alarms:" + memberId;
    }

}

