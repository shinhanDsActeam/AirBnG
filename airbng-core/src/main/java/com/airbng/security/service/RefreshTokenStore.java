package com.airbng.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {
    private final StringRedisTemplate redisTemplate;

    private String key(Long userId) { return "refresh:" + userId;}

    public void save(Long userId, String refreshToken, long ttlMillis) {
        redisTemplate.opsForValue().set(key(userId), refreshToken, Duration.ofMillis(ttlMillis));
    }

    public boolean matches(Long userId, String refreshToken) {
        String saved = redisTemplate.opsForValue().get(key(userId));
        return refreshToken.equals(saved);
    }

    public void delete(Long userId) {
        redisTemplate.delete(key(userId));
    }

}
