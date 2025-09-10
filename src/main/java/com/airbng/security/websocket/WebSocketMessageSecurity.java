package com.airbng.security.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;

@Configuration
public class WebSocketMessageSecurity {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager() {

        return (authentication, message) -> {
            StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
            SimpMessageType type = acc.getMessageType();
            String dest = acc.getDestination();

            // 1) CONNECT/HEARTBEAT/UNSUBSCRIBE/DISCONNECT 또는 목적지 없는 메시지는 항상 허용
            if (type == SimpMessageType.CONNECT
                    || type == SimpMessageType.HEARTBEAT
                    || type == SimpMessageType.UNSUBSCRIBE
                    || type == SimpMessageType.DISCONNECT
                    || dest == null) {
                return new AuthorizationDecision(true);
            }

            // 2) /app/** 로 SEND 하는 건 인증 필요
            if (dest.startsWith("/app/")) {
                return new AuthorizationDecision(authentication.get() != null
                        && authentication.get().isAuthenticated());
            }

            // 3) 구독 목적지(/topic, /queue, /user)도 인증 필요
            if (dest.startsWith("/topic/")
                    || dest.startsWith("/queue/")
                    || dest.startsWith("/user/")) {
                return new AuthorizationDecision(authentication.get() != null
                        && authentication.get().isAuthenticated());
            }

            // 4) 그 외는 허용 (원하면 false로 바꿔 더 보수적으로)
            return new AuthorizationDecision(true);
        };
    }
}