package com.airbng.service;

import com.airbng.domain.base.NotificationType;
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

    private static final long EXPIRE_SECONDS =  60; // 테스트용 1분

    //알림이 발송된 후 24시간 동안 중복 발송 방지 -> 실제 코드
//    private static final long EXPIRE_SECONDS = 24 * 60 * 60;

    //알림이 발송되었는지 획인하기 위함 (레디스에 저장되었는지 확인)
    @Override
    public boolean isSent(Long reservationId, Long receiverId, NotificationType type) {
        String key = buildKey(reservationId, receiverId, type);
        Boolean result = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        log.info("!!!Redis 캐시에 중복 여부 확인: {}, 결과={}", key, result);
        return result;
    }


    @Override
    public boolean tryMarkSent(Long reservationId, Long receiverId, NotificationType type) {
        String key = buildKey(reservationId, receiverId, type);
        // 처음 SET하는 경우에만 true 리턴됨
        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "true", EXPIRE_SECONDS, TimeUnit.SECONDS);
        return Boolean.TRUE.equals(success);
    }


    //알림 발송된 것 레디스에 저장
    @Override
    public void markSent(Long reservationId, Long receiverId, NotificationType type) {
        String key = buildKey(reservationId, receiverId, type);
        redisTemplate.opsForValue().set(key, "true", EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.info("!!!Redis 캐시에 알림 발송 기록 저장: {}", key);
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
    public List<String> getMissedAlarms(Long memberId, String lastEventId) {
        String redisKey = buildAlarmListKey(memberId);
        List<String> all = redisTemplate.opsForList().range(redisKey, 0, -1);
        if (all == null) return List.of();

        return all.stream()
                .map(s -> s.split("\\|", 2))        // [eventId, payload]
                .filter(arr -> arr[0].compareTo(lastEventId) > 0) // lastEventId 이후 알림만 전송
                .map(arr -> arr[1])                  // payload만 반환
                .collect(Collectors.toList());
    }


    @Override
    //알림 저장(value값으로 쌓임) -> sse 연결 시 놓친 알림 전송용
    public String saveAlarm(Long memberId, Object payload) {
        String eventId = UUID.randomUUID().toString();
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
        return eventId;
    }

    //회원별 알림 리스트 키
    private String buildAlarmListKey(Long memberId) {
        return "alarms:" + memberId;
    }

}

