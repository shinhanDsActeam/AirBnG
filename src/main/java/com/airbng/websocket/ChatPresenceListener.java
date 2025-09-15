package com.airbng.websocket;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.util.JwtUtil;
import com.airbng.service.chat.PresenceService;
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
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPresenceListener {

    private final PresenceService presenceService;
    private final SimpMessagingTemplate messaging;
    private final JwtUtil jwtUtil;

    /* -------- helpers -------- */

    /** Principal → userId 안전 추출 */
    private Long extractUserId(Principal u) {
        if (u == null) return null;

        if (u instanceof Authentication auth) {
            Object p = auth.getPrincipal();
            if (p instanceof CustomUserDetails cud) return cud.getId();
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

    /** CONNECT 프레임 수신: 여기서는 로깅만 (Principal이 비어있는 경우가 많음) */
    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        log.debug("[PRESENCE] CONNECT (sid={})", acc.getSessionId());
    }

    /** CONNECTED(ACK) 이후: Principal 보장 → 온라인 처리(+ JWT TTL 연동) */
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

        // --- JWT 남은 시간 계산 ---
        long ttlSeconds = resolveJwtTtlSeconds(acc);
        presenceService.online(userId, sessionId, ttlSeconds);
        log.info("[PRESENCE] CONNECTED userId={} session={} ttl={}s", userId, sessionId, ttlSeconds);

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

    /** CONNECT 프레임 Native Header에서 Authorization 추출 → 남은 TTL(초) */
    private long resolveJwtTtlSeconds(StompHeaderAccessor acc) {
        try {
            // 1) 표준 키
            String header = acc.getFirstNativeHeader(JwtUtil.TOKEN_HEADER); // "Authorization"

            // 2) 일부 클라이언트가 소문자로 보낼 수 있음
            if (header == null) {
                List<String> alt = acc.getNativeHeader(JwtUtil.TOKEN_HEADER.toLowerCase());
                if (alt != null && !alt.isEmpty()) header = alt.get(0);
            }

            if (header == null) {
                // 헤더가 없으면 ACCESS 토큰 기본 만료(초) 리턴
                return JwtUtil.ACCESS_TOKEN_EXPIRATION / 1000L;
            }

            // 3) Bearer 제거
            String token = header.startsWith(JwtUtil.TOKEN_PREFIX)
                    ? header.substring(JwtUtil.TOKEN_PREFIX.length()).trim()
                    : header.trim();

            // 타입/만료 검증
            if (!JwtUtil.TOKEN_TYPE_ACCESS.equals(jwtUtil.getType(token))) {
                log.warn("[PRESENCE] non-access token on CONNECT");
                return JwtUtil.ACCESS_TOKEN_EXPIRATION / 1000L;
            }
            if (jwtUtil.isExpired(token)) return 1L;

            // JwtUtil에 추가해 둔 남은 TTL(초)
            return jwtUtil.remainingSeconds(token);

        } catch (Exception e) {
            log.warn("[PRESENCE] resolveJwtTtlSeconds error: {}", e.getMessage());
            return JwtUtil.ACCESS_TOKEN_EXPIRATION / 1000L;
        }
    }

    public record PresenceEvent(Long userId, boolean online, Instant at) {}
}
