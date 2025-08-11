package com.airbng.config;

import com.airbng.dto.locker.LockerTop5Response;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class RedisMessageSubscriber  implements MessageListener {

    private final Cache<String, LockerTop5Response> localCache;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String channel = new String(message.getChannel(), StandardCharsets.UTF_8);
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        if ("lockerTop5Updated".equals(channel)) {
            if ("invalidate".equals(body)) {
                localCache.invalidate("lockerTop5");
            }
        }

    }
}

