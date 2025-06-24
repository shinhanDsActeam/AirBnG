package com.airbng.interceptor;

import com.airbng.common.response.status.BaseResponseStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;

import static com.airbng.common.response.status.BaseResponseStatus.DDOS_PREVENTION;
import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS;
import static org.junit.jupiter.api.Assertions.*;

class RequestRateLimitInterceptorTest {

    private final RequestRateLimitInterceptor interceptor = new RequestRateLimitInterceptor();

    @Nested
    @DisplayName("첫 요청 허용 테스트")
    class 첫_요청_테스트 {

        @Test
        @DisplayName("첫 요청은 허용되어야 한다")
        void 첫_요청은_허용된다() throws Exception {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/zzim/toggle");
            request.setRemoteAddr("127.0.0.1");
            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            boolean result = interceptor.preHandle(request, response, new Object());

            // then
            assertTrue(result);
            assertEquals(SUCCESS.getHttpStatus(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("IP 기반 중복 요청 차단")
    class 비로그인_사용자_IP기반_차단 {

        @Test
        @DisplayName("너무 빠른 중복 요청은 차단되어야 한다")
        void 빠른_중복_요청은_차단된다() throws Exception {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setRequestURI("/zzim/toggle");
            request.setRemoteAddr("127.0.0.1");
            MockHttpServletResponse response1 = new MockHttpServletResponse();
            MockHttpServletResponse response2 = new MockHttpServletResponse();

            // when
            boolean first = interceptor.preHandle(request, response1, new Object());
            boolean second = interceptor.preHandle(request, response2, new Object());

            // then
            assertTrue(first);
            assertFalse(second);
            assertEquals(DDOS_PREVENTION.getHttpStatus(), response2.getStatus());
        }
    }

    @Nested
    @DisplayName("로그인 사용자 기준 요청 제한")
    class 로그인_사용자_기반_요청_제한 {

        @Test
        @DisplayName("로그인한 사용자의 첫 요청은 통과된다")
        void 로그인한_사용자의_첫_요청은_허용된다() throws Exception {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/lockers/1/members/42/zzim");
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", 42L);
            request.setSession(session);
            MockHttpServletResponse response = new MockHttpServletResponse();

            // when
            boolean result = interceptor.preHandle(request, response, new Object());

            // then
            assertTrue(result);
            assertEquals(SUCCESS.getHttpStatus(), response.getStatus());
        }
    }

    @Nested
    @DisplayName("URI + 사용자 기준 DDOS 방어")
    class URI_사용자_기반_DDOS_방지 {

        @Test
        @DisplayName("같은 사용자가 동일 URI에 대해 5초 이내 두 번 요청하면 차단된다")
        void 동일_사용자의_빠른_요청은_차단된다() throws Exception {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest("POST", "/lockers/1/members/42/zzim");
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", 42L);
            request.setSession(session);

            MockHttpServletResponse response1 = new MockHttpServletResponse();
            MockHttpServletResponse response2 = new MockHttpServletResponse();

            // when
            boolean allowed1 = interceptor.preHandle(request, response1, new Object());
            boolean allowed2 = interceptor.preHandle(request, response2, new Object());

            // then
            assertTrue(allowed1);
            assertFalse(allowed2);
            assertEquals(DDOS_PREVENTION.getHttpStatus(), response2.getStatus());
        }
    }
}
