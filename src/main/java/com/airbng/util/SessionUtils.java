package com.airbng.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionUtils {

    /**
     * 세션에서 memberId를 꺼낸다. 없으면 null 반환
     */
    public static Long getMemberId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;

        Object value = session.getAttribute("memberId");
        return (value instanceof Long) ? (Long) value : null;
    }

    /**
     * 로그인 여부 판단
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getMemberId(request) != null;
    }
}
