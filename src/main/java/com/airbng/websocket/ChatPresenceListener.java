package com.airbng.websocket;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.chat.PresenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
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

    /** Principal/Authentication 에서 userId를 안전하게 추출 */
    private Long extractUserId(StompHeaderAccessor acc) {
        Principal u = acc.getUser();
        if (u == null) return null;

        // 1) Authentication 이면서 CustomUserDetails 인 경우
        if (u instanceof org.springframework.security.core.Authentication auth) {
            Object p = auth.getPrincipal();
            if (p instanceof CustomUserDetails cud) {
                return cud.getId();
            }
        }
        // 2) name이 숫자(userId)로 들어온 경우
        try { return Long.valueOf(u.getName()); } catch (Exception ignore) {}
        return null;
    }

    /** 서버가 CONNECT 프레임을 받고 처리하는 시점 */
    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        Long userId = extractUserId(acc);
        String sessionId = acc.getSessionId();
        if (userId != null && sessionId != null) {
            presenceService.online(userId, sessionId);
            log.info("[PRESENCE] CONNECT userId={} session={}", userId, sessionId);

            // 본인에게도 online 이벤트 통지(선택)
            messaging.convertAndSendToUser(
                    String.valueOf(userId), "/queue/presence",
                    new PresenceEvent(userId, true, Instant.now())
            );
        }
    }

    /** STOMP CONNECTED(Ack) 이후 훅 — 필요시 사용 */
    @EventListener
    public void onConnected(SessionConnectedEvent e) {
        // 필요 없다면 비워두거나 로깅만
         var acc = StompHeaderAccessor.wrap(e.getMessage());
         log.debug("[PRESENCE] CONNECTED {}", acc.getSessionId());
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        Long userId = extractUserId(acc);
        String sessionId = acc.getSessionId();

        if (userId != null && sessionId != null) {
            presenceService.offline(userId, sessionId);
            log.info("[PRESENCE] DISCONNECT userId={} session={}", userId, sessionId);
        } else if (sessionId != null) {
            // Principal이 없더라도 세션ID로 정리
            presenceService.offlineBySessionId(sessionId);
            log.info("[PRESENCE] DISCONNECT (no user) session={}", sessionId);
        }
    }

    public record PresenceEvent(Long userId, boolean online, Instant at) {}
}
