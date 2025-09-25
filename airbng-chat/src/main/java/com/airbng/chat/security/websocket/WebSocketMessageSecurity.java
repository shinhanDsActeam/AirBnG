package com.airbng.chat.security.websocket;

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

            boolean isAuth = authentication.get() != null && authentication.get().isAuthenticated();

            // 1) 목적지 없는 프레임(HEARTBEAT 등) 또는 연결/종료 계열은 허용
            if (type == SimpMessageType.CONNECT
                    || type == SimpMessageType.HEARTBEAT
                    || type == SimpMessageType.UNSUBSCRIBE
                    || type == SimpMessageType.DISCONNECT
                    || dest == null) {
                return new AuthorizationDecision(true);
            }

            // 2) SEND 프레임: /app/** 만 허용(인증 필수). 그 외 목적지는 거부
            if (type == SimpMessageType.MESSAGE) {
                if (dest.startsWith("/app/")) {
                    return new AuthorizationDecision(isAuth);
                }
                // 클라가 /topic,/queue 로 SEND 하는 케이스는 차단
                return new AuthorizationDecision(false);
            }

            // 3) SUBSCRIBE 프레임: 브로커 목적지(/topic,/queue,/user)만 허용(인증 필수)
            if (type == SimpMessageType.SUBSCRIBE) {
                if (dest.startsWith("/topic/")
                        || dest.startsWith("/queue/")
                        || dest.startsWith("/user/")) {
                    return new AuthorizationDecision(isAuth);
                }
                return new AuthorizationDecision(false);
            }

            // 4) 그 외 타입은 보수적으로 거부
            return new AuthorizationDecision(false);
        };
    }
}
