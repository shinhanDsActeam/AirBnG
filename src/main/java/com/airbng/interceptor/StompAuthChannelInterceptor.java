package com.airbng.interceptor;

import com.airbng.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.List;

import static com.airbng.security.util.JwtUtil.*;

@Component
@RequiredArgsConstructor
public class StompAuthChannelInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        var acc = StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(acc.getCommand())) {
            String token = extractBearer(acc.getNativeHeader("Authorization"));
            if (token == null) token = extractBearer(acc.getNativeHeader("authorization"));

            if (token != null && !token.isBlank()) {
                // 동일한 규칙으로 검증 (JwtFilter와 일치)
                if (jwtUtil.isExpired(token))
                    throw new IllegalArgumentException("Expired token");
                if (!TOKEN_TYPE_ACCESS.equals(jwtUtil.getType(token)))
                    throw new IllegalArgumentException("Invalid token type");

                Long userId = jwtUtil.getUserId(token);
                String role = jwtUtil.getRole(token);
                acc.setUser(new StompUserPrincipal(userId, role));
            }
            // 토큰이 없어도 CONNECT 자체는 허용(메시징 보안 설정에서 /app/**는 authenticated)
        }
        return message;
    }

    private String extractBearer(List<String> headers) {
        if (headers == null || headers.isEmpty()) return null;
        String v = headers.get(0);
        return (v != null && v.startsWith(TOKEN_PREFIX)) ? v.substring(TOKEN_PREFIX.length()).trim() : null;
    }

    /** STOMP Principal: name은 userId 문자열 */
    public record StompUserPrincipal(Long id, String role) implements Principal {
        @Override public String getName() { return String.valueOf(id); }
    }
}
