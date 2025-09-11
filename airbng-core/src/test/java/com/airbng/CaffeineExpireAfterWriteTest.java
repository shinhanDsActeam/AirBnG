package com.airbng;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Scheduler;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CacheStats {
 *   hitCount = 2,          // 캐시에 2번 적중했음 (getIfPresent 해서 값 받음)
 *   missCount = 1,         // 캐시에 1번 실패 (null 반환 → 만료됨)
 *   loadSuccessCount = 0,  // loading cache 가 아니라서 없음
 *   loadFailureCount = 0,  // loading cache 가 아니라서 없음
 *   totalLoadTime = 0,     // loading cache 가 아니라서 없음
 *   evictionCount = 1,     // key1 가 만료로 제거됨 → 1건
 *   evictionWeight = 1     // 제거된 항목 weight = 1
 * }
 * */

public class CaffeineExpireAfterWriteTest {
    @Test
    void 스케줄러_정상_작동_테스트() throws InterruptedException {
        Cache<String, String> cache = Caffeine.newBuilder()
                .expireAfterWrite(3, TimeUnit.SECONDS)  // 테스트 5초
                .scheduler(Scheduler.systemScheduler())
                .recordStats()
                .build();

        cache.put("key1", "value1");

        // 바로
        assertEquals(cache.getIfPresent("key1"), "value1");

        // 3초 기다리기
        Thread.sleep(2000);
        assertEquals(cache.getIfPresent("key1"), "value1");

        // 추가로 5초 더 기다리기 (총 8초 경과)
        Thread.sleep(3000);
        assertEquals(cache.getIfPresent("key1"), null);

        System.out.println(cache.stats());
    }
}
