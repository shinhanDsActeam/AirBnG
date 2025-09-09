package com.airbng.websocket;

import com.airbng.interceptor.StompAuthChannelInterceptor.StompUserPrincipal;
import com.airbng.util.chat.RedisSequenceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.*;

import java.time.Instant;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatPresenceListener {

    private final RedisSequenceService redis;
    private final SimpMessagingTemplate messaging;

    @Value("${chat.presence.ttl-seconds:60}")
    private long presenceTtl;

    @EventListener
    public void onConnect(SessionConnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        var p = (StompUserPrincipal) acc.getUser();
        if (p != null) {
            redis.setPresence(p.id(), true, presenceTtl);
            log.info("[PRESENCE] CONNECT userId={} ttl={}s", p.id(), presenceTtl);

            // 본인 개인 큐에 온라인 이벤트 (선택)
            messaging.convertAndSendToUser(
                    String.valueOf(p.id()), "/queue/presence",
                    new PresenceEvent(p.id(), true, Instant.now())
            );
        }
    }

    @EventListener
    public void onDisconnect(SessionDisconnectEvent e) {
        var acc = StompHeaderAccessor.wrap(e.getMessage());
        var p = (StompUserPrincipal) acc.getUser();
        if (p != null) {
            redis.setPresence(p.id(), false, 0);
            log.info("[PRESENCE] DISCONNECT userId={}", p.id());
        }
    }

    public record PresenceEvent(Long userId, boolean online, Instant at) {}
}