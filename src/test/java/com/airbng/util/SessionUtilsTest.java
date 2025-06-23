package com.airbng.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

import static org.junit.jupiter.api.Assertions.*;

class SessionUtilsTest {

    @Nested
    @DisplayName("getMemberId 메서드 테스트")
    class GetMemberIdTest {

        @Test
        @DisplayName("세션에 memberId가 존재할 경우 정상 반환")
        void memberId_정상반환() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", 123L);
            request.setSession(session);

            // when
            Long result = SessionUtils.getMemberId(request);

            // then
            assertEquals(123L, result);
        }

        @Test
        @DisplayName("세션이 없을 경우 null 반환")
        void 세션없음_null반환() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();

            // when
            Long result = SessionUtils.getMemberId(request);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("세션은 있지만 memberId가 없을 경우 null 반환")
        void memberId_없음_null반환() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setSession(new MockHttpSession()); // 빈 세션

            // when
            Long result = SessionUtils.getMemberId(request);

            // then
            assertNull(result);
        }

        @Test
        @DisplayName("memberId가 Long이 아닐 경우 null 반환")
        void memberId_타입불일치_null반환() {
            // given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", "not-a-long"); // 잘못된 타입
            request.setSession(session);

            // when
            Long result = SessionUtils.getMemberId(request);

            // then
            assertNull(result);
        }
    }

    @Nested
    @DisplayName("isLoggedIn 메서드 테스트")
    class IsLoggedInTest {

        @Test
        @DisplayName("memberId가 존재할 경우 true")
        void 로그인된_상태_true() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpSession session = new MockHttpSession();
            session.setAttribute("memberId", 1L);
            request.setSession(session);

            assertTrue(SessionUtils.isLoggedIn(request));
        }

        @Test
        @DisplayName("memberId가 없을 경우 false")
        void 로그인_안됨_false() {
            MockHttpServletRequest request = new MockHttpServletRequest();
            request.setSession(new MockHttpSession());

            assertFalse(SessionUtils.isLoggedIn(request));
        }
    }
}
