package com.airbng.security.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.MessageMatcherDelegatingAuthorizationManager;

@Configuration
public class WebSocketMessageSecurity {

    @Bean
    public AuthorizationManager<Message<?>> messageAuthorizationManager(
            MessageMatcherDelegatingAuthorizationManager.Builder messages) {

        messages
                // CONNECT 프레임은 STOMP 인터셉터에서 JWT 검증하므로 여기선 통과(혹은 authenticated()로 바꿔도 됨)
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.HEARTBEAT,
                        SimpMessageType.DISCONNECT, SimpMessageType.UNSUBSCRIBE).permitAll()

                // 클라이언트 → 서버 (컨트롤러 @MessageMapping)
                .simpDestMatchers("/app/**").authenticated()

                // 서버 → 클라이언트 구독 목적지
                .simpSubscribeDestMatchers("/topic/**", "/queue/**", "/user/**").authenticated()

                // 그 외
                .anyMessage().denyAll();

        return messages.build();
    }
}