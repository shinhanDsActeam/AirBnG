package com.airbng.interceptor;

import com.airbng.util.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.airbng.common.response.status.BaseResponseStatus.DDOS_PREVENTION;

@Slf4j
@Component
public class RequestRateLimitInterceptor implements HandlerInterceptor {

    private static final long LIMIT_MILLIS = 1000; // 1초 제한
    private final Map<String, Long> lastRequestMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String key = getKey(request);
        long now = System.currentTimeMillis();
        long lastTime = lastRequestMap.getOrDefault(key, 0L);

        if (now - lastTime < LIMIT_MILLIS) {
            log.warn("DDOS 차단 - key={}, path={}", key, request.getRequestURI());
            response.setStatus(DDOS_PREVENTION.getHttpStatus());
            response.getWriter().write(DDOS_PREVENTION.getMessage());
            return false;
        }

        lastRequestMap.put(key, now);
        return true;
    }

    private String getKey(HttpServletRequest request) {
        Object memberId = request.getSession().getAttribute("memberId");
        return (memberId != null ? "user:" + memberId : "ip:" + request.getRemoteAddr()) + ":" + request.getRequestURI();
    }
}
