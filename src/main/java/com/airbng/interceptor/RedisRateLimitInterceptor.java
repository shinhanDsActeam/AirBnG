package com.airbng.interceptor;

import com.airbng.util.ResponseUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static com.airbng.common.response.status.BaseResponseStatus.REQUEST_RATE_LIMIT_EXCEEDED;

@Component
@RequiredArgsConstructor
public class RedisRateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    private static final int LIMIT_INTERVAL_SECONDS = 1;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String ip = request.getRemoteAddr();
        String key = "ratelimit:" + ip;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count != null && count == 1L) {
            redisTemplate.expire(key, LIMIT_INTERVAL_SECONDS, TimeUnit.SECONDS);
            return true;
        } else {
            ResponseUtils.writeErrorResponse(response, REQUEST_RATE_LIMIT_EXCEEDED);
            return false;
        }
    }
}