package com.airbng.chat.websocket;

import com.airbng.chat.service.PresenceService;
import com.airbng.platform.security.principal.AirbngPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;
import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPresenceListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messaging;

    /* -------- helpers -------- */

    /** Principal → userId 안전 추출 */
    private Long extractUserId(Principal u) {
        if (u == null) return null;
        if (u instanceof Authentication auth) {
            Object p = auth.getPrincipal();
            if (p instanceof AirbngPrincipal ap) return ap.getId();
        }
        try { return Long.valueOf(u.getName()); } catch (Exception ignore) {}
        return null;
    }

    /** convertAndSendToUser user-name 결정 (Principal.name 우선) */
    private String resolveUserDestinationName(Principal u, Long fallbackUserId) {
        if (u != null && u.getName() != null) return u.getName();
        return String.valueOf(fallbackUserId);
    }

    /** presence 브로드캐스트 (/topic/presence) */
    private void broadcastPresence(Long userId, boolean online) {
        messaging.convertAndSend("/topic/presence",
                new PresenceEvent(userId, online, Instant.now()));
    }

    /* -------- events -------- */

    /** CONNECT 프레임 수신: 참고용 로깅 */
    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        log.debug("[PRESENCE] CONNECT (sid={})", acc.getSessionId());
    }

    /** CONNECTED(ACK) 이후: Principal 보장 → 온라인 등록 */
    @EventListener
    public void onConnected(SessionConnectedEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        Principal pu = (e.getUser() != null) ? e.getUser() : acc.getUser();

        Long userId = extractUserId(pu);
        String sessionId = acc.getSessionId();

        if (userId == null || sessionId == null) {
            log.warn("[PRESENCE] CONNECTED but missing principal/session (user={}, sid={})", pu, sessionId);
            return;
        }

        // TTL 계산 없이 고정 TTL + 핑으로 연장 (PresenceService 내부 정책)
        presenceService.online(userId, sessionId);
        log.info("[PRESENCE] CONNECTED userId={} session={}", userId, sessionId);

        String userName = resolveUserDestinationName(pu, userId);
        messaging.convertAndSendToUser(userName, "/queue/presence",
                new PresenceEvent(userId, true, Instant.now()));
        broadcastPresence(userId, true);
    }

    /** DISCONNECT: 마지막 세션일 때만 offline 브로드캐스트 */
    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        Principal pu = (e.getUser() != null) ? e.getUser() : acc.getUser();
        Long userId = extractUserId(pu);
        String sessionId = acc.getSessionId();

        if (sessionId == null) return;

        if (userId != null) {
            presenceService.offline(userId, sessionId);
            boolean stillOnline = presenceService.isOnline(userId);
            log.info("[PRESENCE] DISCONNECT userId={} session={} (stillOnline={})",
                    userId, sessionId, stillOnline);

            if (!stillOnline) {
                broadcastPresence(userId, false);
                String userName = resolveUserDestinationName(pu, userId);
                messaging.convertAndSendToUser(userName, "/queue/presence",
                        new PresenceEvent(userId, false, Instant.now()));
            }
        } else {
            // Principal을 못 얻은 경우 세션ID 기준 정리
            presenceService.offlineBySessionId(sessionId);
            log.info("[PRESENCE] DISCONNECT (no user) session={}", sessionId);
        }
    }

    public record PresenceEvent(Long userId, boolean online, Instant at) {}
}
