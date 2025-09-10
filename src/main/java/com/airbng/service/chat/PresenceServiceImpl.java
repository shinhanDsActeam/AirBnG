package com.airbng.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final StringRedisTemplate redis;

    private String userKey(long userId) { return "presence:user:" + userId; }
    private String sessionKey(String sessionId) { return "presence:session:" + sessionId; }

    @Override
    public void online(long userId, String sessionId) {
        // 유저 세트에 세션 추가, 세션→유저 매핑 기록
        redis.opsForSet().add(userKey(userId), sessionId);
        redis.opsForValue().set(sessionKey(sessionId), String.valueOf(userId));
        // 필요하면 TTL 전략 적용 가능(세션 키에만 TTL 걸기 등)
         redis.expire(sessionKey(sessionId), Duration.ofHours(12));
    }

    @Override
    public void offline(long userId, String sessionId) {
        redis.opsForSet().remove(userKey(userId), sessionId);
        redis.delete(sessionKey(sessionId));
        // 세트가 비었으면 키 정리(선택)
        Long size = redis.opsForSet().size(userKey(userId));
        if (size != null && size == 0L) {
            redis.delete(userKey(userId));
        }
    }

    @Override
    public boolean isOnline(long userId) {
        Long size = redis.opsForSet().size(userKey(userId));
        return size != null && size > 0;
    }

    @Override
    public Map<Long, Boolean> isOnlineBulk(List<Long> userIds) {
        Map<Long, Boolean> result = new HashMap<>();
        for (Long id : userIds) {
            result.put(id, isOnline(id));
        }
        return result;
    }

    @Override
    public long onlineSessionCount(long userId) {
        Long size = redis.opsForSet().size(userKey(userId));
        return size == null ? 0 : size;
    }

    @Override
    public void offlineBySessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return;

        // 1) 세션 → 유저 찾기
        String skey = sessionKey(sessionId);
        String userIdStr = redis.opsForValue().get(skey);
        if (userIdStr == null) {
            // 이미 정리되었거나 알 수 없는 세션
            redis.delete(skey);
            return;
        }

        long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            // 포맷 이상 시 세션키만 정리
            redis.delete(skey);
            return;
        }

        // 2) 유저 세트에서 세션 제거
        String ukey = userKey(userId);
        redis.opsForSet().remove(ukey, sessionId);

        // 3) 세션키 삭제
        redis.delete(skey);

        // 4) 세션 더 없으면 유저키 삭제(청소)
        Long size = redis.opsForSet().size(ukey);
        if (size != null && size == 0L) redis.delete(ukey);
    }
}