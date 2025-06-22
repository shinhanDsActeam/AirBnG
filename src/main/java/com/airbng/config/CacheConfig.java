package com.airbng.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Configuration
public class CacheConfig {
    /**
     * 기본적으로 event-driven eviction 떄문에
     * 스케줄러를 명시해야 -> 백그라운드에서 주기적 evict 발생
     * */
    @Bean
    public Cache<Long, ReentrantLock> reservationLocks() {
        return Caffeine.newBuilder()
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .scheduler(Scheduler.systemScheduler())
                .build();
    }
}

