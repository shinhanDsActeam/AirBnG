package com.airbng.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.Clock;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.airbng.common.response.status.BaseResponseStatus.DDOS_PREVENTION;

@Slf4j
@Component
public class RequestRateLimitInterceptor implements HandlerInterceptor {

    private static final int MAX_REPEAT_COUNT = 100;
    private static final long BLOCK_DURATION = 30_000L;

    private final Map<String, Integer> requestCountMap = new ConcurrentHashMap<>();
    private final Map<String, Long> blockUntilMap = new ConcurrentHashMap<>();
    private final Clock clock;

    // 기본 생성자
    public RequestRateLimitInterceptor() {
        this(Clock.systemDefaultZone());
    }

    // 테스트용
    public RequestRateLimitInterceptor(Clock clock) {
        this.clock = clock;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        String key = getKey(request);
        long now = clock.millis();

        if (blockUntilMap.containsKey(key)) {
            long blockUntil = blockUntilMap.get(key);
            if (now < blockUntil) {
                response.setStatus(DDOS_PREVENTION.getHttpStatus());
                response.getWriter().write("요청이 너무 많습니다. 30초 동안 차단됩니다.");
                return false;
            }
            blockUntilMap.remove(key);
            requestCountMap.remove(key);
        }

        int count = requestCountMap.getOrDefault(key, 0) + 1;
        requestCountMap.put(key, count);

        if (count > MAX_REPEAT_COUNT) {
            blockUntilMap.put(key, now + BLOCK_DURATION);
            requestCountMap.remove(key);
            response.setStatus(DDOS_PREVENTION.getHttpStatus());
            response.getWriter().write("요청이 너무 많습니다. 30초 동안 차단됩니다.");
            return false;
        }

        return true;
    }

    private String getKey(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object memberId = session.getAttribute("memberId");
            if (memberId != null) {
                return "user:" + memberId + ":" + request.getRequestURI();
            }
        }
        return "ip:" + request.getRemoteAddr() + ":" + request.getRequestURI();
    }
}