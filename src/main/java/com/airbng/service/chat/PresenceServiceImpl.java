package com.airbng.service.chat;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresenceServiceImpl implements PresenceService {

    private final StringRedisTemplate redis;

    private String userKey(long userId) { return "presence:user:" + userId; }
    private String sessionKey(String sessionId) { return "presence:session:" + sessionId; }
//    private String globalKey() { return "presence:online:users"; }

    private static final String KEY_ONLINE_SET = "presence:online:users"; // 온라인 여부 집합
    private static final String KEY_ONLINE_Z = "presence:online:z"; // 최근 활동 정렬 집합

    /** 30초 버킷으로 라운딩된 epochSec */
    private long bucket30s(long nowMillis) {
        long sec = nowMillis / 1000L;
        return (sec / 30L) * 30L;  // …, 0,30,60,90,…
    }

    /** ZSET 점수(버킷)가 바뀔 때만 갱신 */
    private void upsertBucketIfChanged(long userId, long bucket) {
        String member = String.valueOf(userId);
        Double cur = redis.opsForZSet().score(KEY_ONLINE_Z, member);
        long curBucket = (cur == null) ? Long.MIN_VALUE : cur.longValue();
        if (bucket > curBucket) {
            redis.opsForZSet().add(KEY_ONLINE_Z, member, bucket);
        }
        // 온라인 집합은 멱등
        redis.opsForSet().add(KEY_ONLINE_SET, member);
    }

    /** JWT 남은 수명(ttlSeconds) 만큼 세션키를 살려둠 */
    @Override
    public void online(long userId, String sessionId, long ttlSeconds) {
        if (ttlSeconds <= 0) ttlSeconds = 60 * 60; // 안전한 디폴트(1h)
        // 1) 유저 세트에 세션 추가
        redis.opsForSet().add(userKey(userId), sessionId);
        // 2) 세션키 ← TTL = JWT 남은 시간
        redis.opsForValue().set(sessionKey(sessionId), String.valueOf(userId), Duration.ofSeconds(ttlSeconds));
        // 3) 최근활동 버킷
        upsertBucketIfChanged(userId, bucket30s(System.currentTimeMillis()));
    }

    /** 외부에서 핑/하트비트가 들어올 때 호출(30초 단위로만 반영) */
    @Override
    public void touch(long userId) {
        long bucket = bucket30s(System.currentTimeMillis());
        upsertBucketIfChanged(userId, bucket);
    }

    @Override
    public void offline(long userId, String sessionId) {
        redis.opsForSet().remove(userKey(userId), sessionId);
        redis.delete(sessionKey(sessionId));
        // 세션이 하나도 없으면 전체 정리
        ensureUserClean(userId);
    }

    @Override
    public boolean isOnline(long userId) {
        return ensureUserClean(userId);
    }

    @Override
    public Map<Long, Boolean> isOnlineBulk(List<Long> userIds) {
        Map<Long, Boolean> map = new HashMap<>();
        for (Long id : userIds) map.put(id, ensureUserClean(id));
        return map;
    }

    @Override
    public long onlineSessionCount(long userId) {
        // 유효 세션만 카운트
        String ukey = userKey(userId);
        var sids = redis.opsForSet().members(ukey);
        if (sids == null || sids.isEmpty()) return 0;
        long cnt = 0;
        for (String sid : sids){
            Boolean ok = redis.hasKey(sessionKey(sid));
            if (ok != null && ok) cnt++;
            else redis.opsForSet().remove(ukey, sid);
        }
        if (cnt == 0) ensureUserClean(userId);
        return cnt;
    }

    @Override
    public void offlineBySessionId(String sessionId) {
        if (sessionId == null || sessionId.isBlank()) return;
        String uid = redis.opsForValue().get(sessionKey(sessionId));
        redis.delete(sessionKey(sessionId));
        if (uid == null) return;
        try {
            long userId = Long.parseLong(uid);
            redis.opsForSet().remove(userKey(userId), sessionId);
            ensureUserClean(userId);
        } catch (NumberFormatException ignore) {}
    }

    /** 죽은 세션 멤버를 정리하고, 남은 세션이 없으면 온라인/정렬 집합에서도 제거 */
    private boolean ensureUserClean(long userId){
        String ukey = userKey(userId);
        var sids = redis.opsForSet().members(ukey);
        if (sids == null || sids.isEmpty()){
            redis.delete(ukey);
            redis.opsForSet().remove(KEY_ONLINE_SET, String.valueOf(userId));
            redis.opsForZSet().remove(KEY_ONLINE_Z, String.valueOf(userId));
            return false;
        }
        boolean anyAlive = false;
        for (String sid : sids){
            Boolean exist = redis.hasKey(sessionKey(sid));
            if (exist == null || !exist){
                redis.opsForSet().remove(ukey, sid); // stale 제거
            } else {
                anyAlive = true;
            }
        }
        if (!anyAlive){
            redis.delete(ukey);
            redis.opsForSet().remove(KEY_ONLINE_SET, String.valueOf(userId));
            redis.opsForZSet().remove(KEY_ONLINE_Z, String.valueOf(userId));
        }
        return anyAlive;
    }

    /** (기존 단순 리스트) 최신순 상위 N명 반환 */
    @Override
    public List<Long> onlineUserIds(int limit) {
        var range = redis.opsForZSet().reverseRange(KEY_ONLINE_Z, 0, Math.max(0, limit-1));
        if (range == null) return List.of();
        return range.stream()
                .map(Long::valueOf)
                .filter(this::isOnline) // 실제 유효성 체크
                .collect(Collectors.toList());
    }

    /** 페이지 조회: 최근 활동순 내림차순 */
    @Override
    public List<Long> onlineUserIdsPage(int offset, int size) {
        long start = Math.max(0, offset);
        long end   = start + Math.max(0, size-1);
        var range = redis.opsForZSet().reverseRange(KEY_ONLINE_Z, start, end);
        if (range == null) return List.of();
        return range.stream()
                .map(Long::valueOf)
                .filter(this::isOnline) // 페이지에도 필터
                .collect(Collectors.toList());
    }

    @Override
    public long onlineUserCount() {
        var all = redis.opsForSet().members(KEY_ONLINE_SET);
        if (all == null) return 0;
        return all.stream().map(Long::valueOf).filter(this::isOnline).count();
    }

}