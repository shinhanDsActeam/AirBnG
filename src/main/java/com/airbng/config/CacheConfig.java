package com.airbng.config;

import com.airbng.dto.locker.LockerTop5Response;
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

    /**
     * 1시간마다 만료
     * 백그라운드 옵션제거
     * 트래픽 있을 때만 lazy load하고, 없으면 그대로 놔둬도 됨
     * 쓰기시점
     * */
    @Bean
    public Cache<String, LockerTop5Response> lockerTop5Cache() {
        return Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .build();
    }
}

