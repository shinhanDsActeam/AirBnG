package com.airbng.service;

import com.airbng.domain.base.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationAlarmCacheServiceImpl implements ReservationAlarmCacheService{

    private final RedisTemplate<String, String> redisTemplate;
    private static final long EXPIRE_SECONDS =  60; // 테스트용 1분

//      이게 찐 코드
//    private static final long EXPIRE_SECONDS = 24 * 60 * 60;

//    public ReservationAlarmCacheServiceImpl(RedisTemplate<String, String> redisTemplate) {
//        this.redisTemplate = redisTemplate;
//    }


    //알림이 발송되었는지 획인하기 위함 (레디스에 저장되었는지 확인)
    @Override
    public boolean isSent(Long reservationId, Long receiverId, NotificationType type) {
        String key = buildKey(reservationId, receiverId, type);
        Boolean result = Boolean.TRUE.equals(redisTemplate.hasKey(key));
        log.info("🔍 Redis 캐시에 중복 여부 확인: {}, 결과={}", key, result);
        return result;
    }

    //알림 발송된 것 레디스에 저장
    @Override
    public void markSent(Long reservationId, Long receiverId, NotificationType type) {
        String key = buildKey(reservationId, receiverId, type);
        redisTemplate.opsForValue().set(key, "true", EXPIRE_SECONDS, TimeUnit.SECONDS);
        log.info("🔔 Redis 캐시에 알림 발송 기록 저장: {}", key);
    }

    private String buildKey(Long reservationId, Long receiverId, NotificationType type) {
        return String.format("alarm:%d:%d:%s", reservationId, receiverId, type.name());
    }
}
