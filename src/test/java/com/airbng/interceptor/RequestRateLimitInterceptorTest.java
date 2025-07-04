package com.airbng.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

import static com.airbng.common.response.status.BaseResponseStatus.DDOS_PREVENTION;
import static org.junit.jupiter.api.Assertions.*;

class RequestRateLimitInterceptorTest {

    private RequestRateLimitInterceptor interceptor;
    private MutableClock clock;

    @BeforeEach
    void setUp() {
        clock = new MutableClock();
        interceptor = new RequestRateLimitInterceptor(clock);
    }

    @Nested
    @DisplayName("기본 요청 허용 테스트")
    class 기본_요청_테스트 {

        @Test
        @DisplayName("첫 요청은 허용되어야 한다")
        void 첫_요청은_허용된다() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/zzim/toggle");
            request.setRemoteAddr("127.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();

            boolean result = interceptor.preHandle(request, response, new Object());

            assertTrue(result);
        }
    }

    @Nested
    @DisplayName("연속 요청 차단 테스트")
    class 연속_요청_차단 {

        @Test
        @DisplayName("동일 사용자 100번 연속 요청 후 차단된다")
        void 동일_사용자_100번_연속_요청_후_차단() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/lockers/1/members/42/zzim");
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", 42L);
            request.setSession(session);

            for (int i = 1; i < 101; i++) {
                boolean result = interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
                assertTrue(result, "요청 #" + i + " 은 허용되어야 합니다.");
            }

            // 101번째는 차단되어야 함
            MockHttpServletResponse blockedResponse = new MockHttpServletResponse();
            blockedResponse.setCharacterEncoding("UTF-8");
            boolean blocked = interceptor.preHandle(request, blockedResponse, new Object());
            assertFalse(blocked);
            assertEquals(DDOS_PREVENTION.getHttpStatus(), blockedResponse.getStatus());
            assertEquals("요청이 너무 많습니다. 30초 동안 차단됩니다.", blockedResponse.getContentAsString());
        }
    }

    @Nested
    @DisplayName("차단 해제 테스트")
    class 차단_해제_테스트 {

        @Test
        @DisplayName("30초가 지나면 차단 해제되어 다시 요청 가능하다")
        void 차단_후_30초_경과_후_해제() throws Exception {
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/lockers/1/members/42/zzim");
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", 42L);
            request.setSession(session);

            for (int i = 0; i < 100; i++) {
                interceptor.preHandle(request, new MockHttpServletResponse(), new Object());
            }

            MockHttpServletResponse blocked = new MockHttpServletResponse();
            interceptor.preHandle(request, blocked, new Object());

            clock.forward(31000); // 31초 지난 것으로 설정

            MockHttpServletRequest newRequest = new MockHttpServletRequest("POST", "/lockers/1/members/42/zzim");
            newRequest.setSession(session);
            MockHttpServletResponse responseAfterBlock = new MockHttpServletResponse();

            boolean allowed = interceptor.preHandle(newRequest, responseAfterBlock, new Object());

            assertTrue(allowed);
        }
    }

    static class MutableClock extends Clock {
        private long millis = System.currentTimeMillis();

        public void forward(long millisToAdd) {
            this.millis += millisToAdd;
        }

        @Override
        public ZoneId getZone() {
            return ZoneId.systemDefault();
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return this;
        }

        @Override
        public Instant instant() {
            return Instant.ofEpochMilli(millis);
        }
    }
}
