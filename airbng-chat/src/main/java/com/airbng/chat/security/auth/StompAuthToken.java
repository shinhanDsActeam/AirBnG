package com.airbng.chat.security.auth;

import com.airbng.platform.security.principal.AirbngPrincipal;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class StompAuthToken extends UsernamePasswordAuthenticationToken {
    private final Long userId;

    public StompAuthToken(AirbngPrincipal principal) {
        super(principal, null, principal.getAuthorities());
        this.userId = principal.getId();
    }

    @Override
    public String getName() {
        // user-destination 라우팅 키를 userId로 고정
        return String.valueOf(userId);
    }
}
