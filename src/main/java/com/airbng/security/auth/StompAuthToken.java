package com.airbng.security.auth;

import com.airbng.security.domain.CustomUserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class StompAuthToken extends UsernamePasswordAuthenticationToken {
    private final Long userId;

    public StompAuthToken(CustomUserDetails principal) {
        super(principal, null, principal.getAuthorities());
        this.userId = principal.getId();
    }

    @Override
    public String getName() {
        // user-destination 라우팅 키를 userId로 고정
        return String.valueOf(userId);
    }
}
