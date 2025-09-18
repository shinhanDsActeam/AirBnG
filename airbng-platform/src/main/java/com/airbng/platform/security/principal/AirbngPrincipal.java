package com.airbng.platform.security.principal;

import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public interface AirbngPrincipal {
    Long getId();
    String getNickname();
    String getUsername();
    String getUserProfileUrl();
    Collection<? extends GrantedAuthority> getAuthorities();
}