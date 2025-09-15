package com.airbng.service.chat;

import java.util.List;
import java.util.Map;

public interface PresenceService {
    /** STOMP CONNECT 시 호출 (세션 온라인 등록)*/
    void online(long userId, String sessionId, long ttlSeconds);

    /** STOMP DISCONNECT 시 호출 (세션 오프라인 해제) */
    void offline(long userId, String sessionId);

    /** 유저가 한 개 이상 세션을 갖고 있으면 온라인 */
    boolean isOnline(long userId);

    /** 여러 명 온라인 여부 조회 */
    Map<Long, Boolean> isOnlineBulk(List<Long> userIds);

    /** 온라인 세션 수(디버그/모니터링용) */
    long onlineSessionCount(long userId);

    /** user가 null로 오는 등 세션만 있을 때 오프라인 처리 */
    void offlineBySessionId(String sessionId);

    /** 온라인 유저 id 리스트(최대 limit명) */
    List<Long> onlineUserIds(int limit);

    /** 온라인 유저 ID 페이지 조회 (최근 활동순) */
    List<Long> onlineUserIdsPage(int offset, int size);

    /** 지금 접속한 온라인 유저 수 */
    long onlineUserCount();

    /** 최근활동 갱신(30초 버킷 기준) */
    void touch(long userId);
}