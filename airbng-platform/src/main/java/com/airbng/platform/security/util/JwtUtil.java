package com.airbng.platform.security.util;

import com.airbng.platform.security.service.RefreshTokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String TOKEN_TYPE_ACCESS = "access";
    public static final String TOKEN_TYPE_REFRESH = "refresh";
    public static final String TOKEN_TYPE_SSE = "sse";
    public static final Long ACCESS_TOKEN_EXPIRATION = 3600000L;
    public static final Long REFRESH_TOKEN_EXPIRATION = 86400000L;

    private SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}") String secret, RefreshTokenStore refreshTokenStore) {
        this.secretKey = new SecretKeySpec(
                secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS512.key().build().getAlgorithm()
        );
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getType(String token) { return parseClaims(token).get("type", String.class);}

    public Long getUserId(String token) { return parseClaims(token).get("id", Long.class);}

    public String getRole(String token) { return parseClaims(token).get("role", String.class);}

    public Boolean isExpired(String token) {return parseClaims(token).getExpiration().before(new Date());}

    public String createJwtToken(String type, Long userId, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("type", type)
                .claim("id", userId)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    /**
     * secure(true) : https통신만 가능
     * sameSite("None") : cross-site(서로다른 도메인/포트)에서 쿠키를 붙여야할때
     */
    public ResponseCookie createCookie(String key, String value) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60 * 60 * 10)
                .path("/")
                .build();
    }

    /**
     * SSE 전용 쿠키를 설정
     */
    public ResponseCookie createSseCookie(String key, String value) {
        return ResponseCookie.from(key,value)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .maxAge(60*60*10)
                .path("/")
                .build();
    }

    // TODO : 채팅 - 온라인 유저 세션 남은 시간 게산용
    public long remainingSeconds(String token) {
        long remainMs = parseClaims(token).getExpiration().getTime() - System.currentTimeMillis();
        return Math.max(1L, remainMs / 1000L);
    }
}

