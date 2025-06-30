package com.airbng.util;

import com.airbng.common.exception.MemberException;
import com.airbng.common.exception.SessionException;
import com.airbng.common.response.status.BaseResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.airbng.common.response.status.BaseResponseStatus.*;

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

    public static Long getLoginMemberId(HttpSession session) {
        if (session == null) {
            throw new SessionException(SESSION_EXPIRED);
        }

        Object memberId = session.getAttribute("memberId");
        if (memberId == null) {
            throw new SessionException(SESSION_NOT_FOUND);
        }

        if (!(memberId instanceof Long)) {
            throw new SessionException(SESSION_INVALID_TYPE);
        }
        return (Long) memberId;
    }
}
