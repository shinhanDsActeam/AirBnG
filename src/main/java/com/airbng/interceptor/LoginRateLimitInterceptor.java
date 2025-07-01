package com.airbng.interceptor;

import com.airbng.common.response.BaseResponse;
import com.airbng.util.ResponseUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.airbng.common.response.status.BaseResponseStatus.LOGIN_RATE_LIMIT_EXCEEDED;

@Component
@RequiredArgsConstructor
public class LoginRateLimitInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!request.getRequestURI().contains("/members/login")) return true;

        // JSON body에서 직접 email 추출
        String body = new BufferedReader(request.getReader())
                .lines().collect(Collectors.joining());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(body);
        String email = root.has("email") ? root.get("email").asText() : null;

        System.out.println("[Interceptor] 추출된 email = " + email);

        if (email == null || email.isEmpty()) {
            System.err.println("[Interceptor] loginEmail 누락 – 요청 거부");
            response.setStatus(HttpStatus.BAD_REQUEST.value());
            response.getWriter().write("이메일 누락");
            return false;
        }

        String key = "login:limit:" + email;
        Long count = redisTemplate.opsForValue().increment(key);
        System.out.println("Redis 로그인 시도 횟수: " + count + " (key: " + key + ")");

        if (count != null && count == 1) {
            redisTemplate.expire(key, 60, TimeUnit.SECONDS);
        }

        if (count != null && count > 5) {
            ResponseUtils.writeErrorResponse(response, LOGIN_RATE_LIMIT_EXCEEDED);
            return false;
        }

        return true;
    }


}