package com.airbng.core.config;

import com.airbng.core.auth.CustomUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;
import java.util.function.Function;
import com.airbng.core.domain.Member;

/**
 * Jwt claim으로부터 CustomUserDetails 객체를 생성하는 팩토리 빈
 * platform 모듈의 SecurityConfig에 주입되어 platform 모듈의 JWT 인증 필터에서 사용됨
 */
@Configuration
public class SecurityPrincipalFactoryConfig {

    @Bean
    public Function<Map<String,Object>, UserDetails> principalFactory() {
        return claims -> {
            Long userId = (Long) claims.get("userId");
            String role = (String) claims.get("role");

            return new CustomUserDetails(new Member(userId, role));
        };
    }
}