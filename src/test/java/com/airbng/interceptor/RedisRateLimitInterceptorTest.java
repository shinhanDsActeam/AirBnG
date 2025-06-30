// 테스트용 인터셉터 테스트 클래스 예시
package com.airbng.interceptor;

import com.airbng.common.response.BaseResponse;
import com.airbng.interceptor.dto.SimpleBaseResponse;
import com.airbng.util.SessionUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.util.concurrent.TimeUnit;

import static com.airbng.common.response.status.BaseResponseStatus.REQUEST_RATE_LIMIT_EXCEEDED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisRateLimitInterceptorTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisRateLimitInterceptor interceptor;

    public RedisRateLimitInterceptorTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Nested
    @DisplayName("Redis Rate Limit 테스트")
    class RateLimit {

        @Test
        @DisplayName("첫 요청은 허용되어야 한다")
        void allow_first_request() throws Exception {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");
            request.setRemoteAddr("127.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.increment("ratelimit:127.0.0.1")).thenReturn(1L);

            // when
            boolean allowed = interceptor.preHandle(request, response, null);

            // then
            assertTrue(allowed);
            verify(valueOperations).increment("ratelimit:127.0.0.1");
            verify(redisTemplate).expire("ratelimit:127.0.0.1", 1, TimeUnit.SECONDS);
        }

        @Test
        @DisplayName("두 번째 요청은 차단되어야 한다")
        void block_second_request() throws Exception {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/test");
            request.setRemoteAddr("127.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();

            when(redisTemplate.opsForValue()).thenReturn(valueOperations);
            when(valueOperations.increment("ratelimit:127.0.0.1")).thenReturn(2L);

            // when
            boolean allowed = interceptor.preHandle(request, response, null);

            // then
            assertFalse(allowed);
            assertEquals(REQUEST_RATE_LIMIT_EXCEEDED.getHttpStatus(), response.getStatus());

            // 응답 본문 메시지 검증
            String content = response.getContentAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            SimpleBaseResponse parsed = objectMapper.readValue(content, SimpleBaseResponse.class);

            assertEquals(REQUEST_RATE_LIMIT_EXCEEDED.getMessage(), parsed.getMessage());
            System.out.println("응답 메세지: " + parsed.getMessage());
        }

    }
}
