package com.airbng.chat.util;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisSequenceService {

    private final StringRedisTemplate redis;

    /* ---------- Key Builders ---------- */
    private static String seqKey(String convId)         { return "conv:" + convId + ":seq"; }
    private static String presenceKey(long userId)      { return "presence:" + userId; }
    private static String typingKey(String convId, long userId) { return "typing:" + convId + ":" + userId; }

    /* ---------- Message sequence (atomic INCR) ---------- */
    /** 대화방 시퀀스 증가. 키가 없으면 Redis INCR 규칙에 따라 1부터 시작 */
    public long nextMessageSeq(String convId) {
        return Optional.ofNullable(redis.opsForValue().increment(seqKey(convId))).orElse(1L);
    }

    /** 현재 시퀀스 조회 (없으면 0) */
    public long getCurrentSeq(String convId) {
        String v = redis.opsForValue().get(seqKey(convId));
        return (v == null) ? 0L : Long.parseLong(v);
    }

    /** 시퀀스 초기화 (테스트/리셋 용) */
    public void resetSeq(String convId) {
        redis.delete(seqKey(convId));
    }

    /* ---------- Presence (online status with TTL) ---------- */
    /** 온라인 표시 + TTL 설정 (예: 60초). 하트비트/재접속 시 갱신해주면 됨 */
    public void setPresence(long userId, boolean online, long ttlSeconds) {
        String key = presenceKey(userId);
        if (online) {
            redis.opsForValue().set(key, "online", Duration.ofSeconds(ttlSeconds));
        } else {
            redis.delete(key);
        }
    }

    /** 현재 온라인인지 여부 */
    public boolean isOnline(long userId) {
        String v = redis.opsForValue().get(presenceKey(userId));
        return "online".equals(v);
    }

    /** TTL 연장(하트비트) */
    public void heartbeat(long userId, long ttlSeconds) {
        redis.expire(presenceKey(userId), Duration.ofSeconds(ttlSeconds));
    }

    /* ---------- Typing indicator ---------- */
    /** 타이핑 on/off (짧은 TTL 권장: 3~5초) */
    public void setTyping(String convId, long userId, boolean typing, long ttlSeconds) {
        String key = typingKey(convId, userId);
        if (typing) {
            redis.opsForValue().set(key, "1", Duration.ofSeconds(ttlSeconds));
        } else {
            redis.delete(key);
        }
    }

    /** 타이핑중인지 여부 */
    public boolean isTyping(String convId, long userId) {
        return Boolean.TRUE.equals(redis.hasKey(typingKey(convId, userId)));
    }
}
