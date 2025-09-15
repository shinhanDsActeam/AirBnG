package com.airbng.interceptor;

import com.airbng.security.auth.StompAuthToken;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.service.CustomUserDetailsService;
import com.airbng.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private static final String SESSION_AUTH_KEY = "WS_AUTH"; // 세션에 보관할 키

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService uds;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor acc = StompHeaderAccessor.wrap(message);
        if (acc == null) return message;

        // 중요: 변경사항 유지
        acc.setLeaveMutable(true);

        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            String token = resolveBearer(acc);
            if (token == null) {
                throw new BadCredentialsException("Missing Authorization");
            }
            if (jwtUtil.isExpired(token)) throw new BadCredentialsException("Expired token");
            if (!JwtUtil.TOKEN_TYPE_ACCESS.equals(jwtUtil.getType(token)))
                throw new BadCredentialsException("Invalid token type");

            Long userId = jwtUtil.getUserId(token);
            var principal = (CustomUserDetails) uds.loadUserById(userId);
            Authentication auth = new StompAuthToken(principal);

            acc.setUser(auth);
            acc.getSessionAttributes().put(SESSION_AUTH_KEY, auth);
        }
        else if (StompCommand.SEND.equals(acc.getCommand())
                || StompCommand.SUBSCRIBE.equals(acc.getCommand())) {

            Authentication auth = null;
            if (acc.getUser() instanceof Authentication a) {
                auth = a;
            } else {
                Object saved = acc.getSessionAttributes() != null
                        ? acc.getSessionAttributes().get(SESSION_AUTH_KEY) : null;
                if (saved instanceof Authentication a) {
                    auth = a;
                    acc.setUser(a); // Principal 주입 원천
                }
            }
            if (auth != null) {
                var ctx = SecurityContextHolder.createEmptyContext();
                ctx.setAuthentication(auth);
                SecurityContextHolder.setContext(ctx);
            }
        }

        // 중요: 변경된 헤더로 새 메시지 리턴 (leaveMutable만으로도 되지만 안전빵)
        return MessageBuilder
                .createMessage(message.getPayload(), acc.getMessageHeaders());
    }

    private String resolveBearer(StompHeaderAccessor acc) {
        String h = acc.getFirstNativeHeader("Authorization");
        if (h == null) h = acc.getFirstNativeHeader("authorization");
        if (h != null && h.startsWith(JwtUtil.TOKEN_PREFIX)) {
            return h.substring(JwtUtil.TOKEN_PREFIX.length()).trim();
        }
        return null;
    }
}