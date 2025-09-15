package com.airbng.config;

import com.airbng.interceptor.StompAuthChannelInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.messaging.access.intercept.AuthorizationChannelInterceptor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.security.messaging.context.SecurityContextChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

import java.util.Arrays;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthChannelInterceptor authInterceptor;
    private final AuthorizationManager<Message<?>> messageAuthorizationManager;

    @Value("${websocket.stomp.endpoint:/ws-chat}")
    private String endpoint;

    @Value("${websocket.stomp.app-destination-prefix:/app}")
    private String appPrefix;

    /**
     * 콤마(,)로 구분된 화이트리스트
     * 예: http://localhost:3000,http://localhost:3001,https://www.airbng.store,https://airbng.store
     */
    @Value("${websocket.stomp.allowed-origins:*}")
    private String allowedOriginsCsv;

    /** 브로커 하트비트용 스케줄러 */
    @Bean
    public TaskScheduler brokerTaskScheduler() {
        ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
        ts.setPoolSize(1); // 하트비트만이면 1개로 충분
        ts.setThreadNamePrefix("ws-heartbeat-");
        ts.initialize();
        return ts;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        String[] origins = Arrays.stream(allowedOriginsCsv.split("\\s*,\\s*"))
                .filter(s -> !s.isBlank())
                .toArray(String[]::new);

        // 운영에선 명시적 화이트리스트 권장
        registry.addEndpoint(endpoint)
                .setAllowedOrigins(origins)
                .withSockJS(); // SockJS 안 쓰면 제거
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.setApplicationDestinationPrefixes(appPrefix);
        registry.enableSimpleBroker("/topic", "/queue")
                .setHeartbeatValue(new long[]{10_000, 10_000}) // [서버→클라, 클라→서버] 10s
                .setTaskScheduler(brokerTaskScheduler());
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(
                // 1) CONNECT에서 JWT 검증 + acc.setUser(auth)
                authInterceptor,
                // 2) simpUser → SecurityContext로 전파 (매 메시지마다)
                new SecurityContextChannelInterceptor(),
                // 3) 권한 검사
                new AuthorizationChannelInterceptor(messageAuthorizationManager)
        );
    }

    /** 대용량/느린 네트워크 대비 전송 한도 */
    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration reg) {
        reg.setMessageSizeLimit(256 * 1024);     // 256KB (STOMP frame payload)
        reg.setSendBufferSizeLimit(512 * 1024);  // 512KB
        reg.setSendTimeLimit(30_000);            // 30s
    }

    /**
     * (선택) 순수 WebSocket 버퍼 한도 — SockJS 미사용/네이티브만 쓸 때 유용
     * 서버 컨테이너 레벨에서 텍스트/바이너리 버퍼 조정
     */
    @Bean
    public ServletServerContainerFactoryBean webSocketContainer() {
        var c = new org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean();
        c.setMaxTextMessageBufferSize(512 * 1024);
        c.setMaxBinaryMessageBufferSize(512 * 1024);
        c.setMaxSessionIdleTimeout(60_000L); // 60s idle timeout (선택)
        return c;
    }
}
