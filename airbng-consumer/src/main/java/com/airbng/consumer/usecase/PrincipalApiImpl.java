package com.airbng.consumer.usecase;

import com.airbng.api.security.PrincipalApi;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.airbng.consumer.auth.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalApiImpl implements PrincipalApi {

    private final CustomUserDetailsService uds;

    @Override
    public AirbngPrincipal loadById(Long userId) {
        // 내부적으로 CustomUserDetails를 쓰지만 바깥(다른 모듈)은 모름
        return (AirbngPrincipal) uds.loadUserById(userId);
    }
}