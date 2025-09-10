package com.airbng.service.chat;

import java.util.List;
import java.util.Map;

public interface PresenceService {
    /** STOMP CONNECT 시 호출 (세션 온라인 등록) */
    void online(long userId, String sessionId);

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
}