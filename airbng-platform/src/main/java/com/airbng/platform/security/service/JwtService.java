package com.airbng.platform.security.service;

import com.airbng.platform.common.exception.SessionException;
import com.airbng.platform.security.dto.TokenResponse;
import com.airbng.platform.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;
import static com.airbng.platform.security.util.JwtUtil.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtUtil jwtUtil;
    private final RefreshTokenStore  refreshTokenStore;

    @Transactional
    public TokenResponse reissueToken(HttpServletRequest request) {
        log.info("[토큰 재발급 요청]");
        String refreshToken = extractRefreshToken(request);
        Long userId = jwtUtil.getUserId(refreshToken);
        if (refreshToken == null) {
            log.error("[토큰 재발급 요청] 리프레시 토큰 없습니다");
            throw new SessionException(REFRESH_TOKEN_NOT_FOUND);
        }

        validateRefreshToken(refreshToken);

        Boolean isRefreshTokenExist = refreshTokenStore.matches(userId, refreshToken);
        if (!isRefreshTokenExist) {
            log.error("[토큰 재발급] 리프레시 토큰이 존재하지 않습니다.");
            throw new SessionException(REFRESH_TOKEN_NOT_FOUND);
        }

        String role = jwtUtil.getRole(refreshToken);

        String newAccessToken = jwtUtil.createJwtToken(TOKEN_TYPE_ACCESS, userId, role, ACCESS_TOKEN_EXPIRATION);
        String newRefreshToken = jwtUtil.createJwtToken(TOKEN_TYPE_REFRESH, userId, role, REFRESH_TOKEN_EXPIRATION);

        refreshTokenStore.delete(userId);

        addRefreshToken(userId, newRefreshToken, REFRESH_TOKEN_EXPIRATION);

        log.info("[토큰 재발급 요청] 토큰 재발급 완료");
        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    private String extractRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(TOKEN_TYPE_REFRESH)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    private void validateRefreshToken(String refreshToken) {
        try {
            jwtUtil.isExpired(refreshToken);
        } catch (ExpiredJwtException e) {
            log.error("[토큰 재발급] 리프레시 토큰이 만료되었습니다.");
            throw new SessionException(EXPIRED_TOKEN);
        }

        String type = jwtUtil.getType(refreshToken);
        if (!type.equals(TOKEN_TYPE_REFRESH)) {
            log.error("[토큰 재발급 요청] 잘못된 리프레시 토큰입니다.");
            throw new SessionException(INVALID_TOKEN);
        }
    }

    @Transactional
    public void addRefreshToken(Long userId, String refreshToken, Long expiredMs) {
        refreshTokenStore.save(userId, refreshToken, expiredMs);
    }
}
