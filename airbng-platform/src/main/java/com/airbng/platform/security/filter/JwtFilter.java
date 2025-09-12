package com.airbng.platform.security.filter;

import com.airbng.platform.common.response.status.BaseResponseStatus;
import com.airbng.platform.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;

import static com.airbng.platform.security.util.JwtUtil.*;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(TOKEN_HEADER);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            String accessToken = request.getHeader(TOKEN_HEADER);
            if (accessToken == null || !accessToken.startsWith(TOKEN_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            accessToken = accessToken.substring(7).trim();
            validateToken(accessToken);
            setAuthentication(accessToken);

        } catch (MalformedJwtException e) {
            log.error("[JWT 필터] 토큰 검증 실패 : 잘못된 토큰 형식");
            request.setAttribute("JWT_ERROR_CODE", BaseResponseStatus.INVALID_TOKEN);
            throw new MalformedJwtException("잘못된 토큰 형식");
        } catch (ExpiredJwtException e) {
            log.error("[JWT 필터] 토큰 검증 실패 : 만료된 토큰");
            request.setAttribute("JWT_ERROR_CODE",  BaseResponseStatus.EXPIRED_TOKEN);
            throw new ExpiredJwtException(null, null, "만료된 토큰");
        } catch (SignatureException e) {
            log.error("[JWT 필터] 토큰 검증 실패 : 잘못된 서명");
            request.setAttribute("JWT_ERROR_CODE", BaseResponseStatus.INVALID_SIGNATURE);
            throw new SignatureException("잘못된 서명");
        }
        filterChain.doFilter(request, response);
    }

    private void validateToken(String token) {
        if (jwtUtil.isExpired(token)) {
            log.error("[JWT 필터] 토큰 검증 실패 : 만료된 토큰");
            throw new ExpiredJwtException(null, null, "만료된 토큰");
        }
        String type = jwtUtil.getType(token);
        if (!TOKEN_TYPE_ACCESS.equals(type)) {
            log.error("[JWT 필터] 토큰 검증 실패 : 잘못된 토큰 타입");
            throw new MalformedJwtException("잘못된 토큰 타입");
        }
    }

    @Autowired(required = false)
    private ObjectProvider<Function<Map<String,Object>, UserDetails>> principalFactoryProvider;

    private void setAuthentication(String token) {
        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        var claims = Map.<String,Object>of(
                "userId", userId,
                "role", role
        );
        log.info("[JWT 필터] 토큰 검증 성공 - ID: {}, 역할: {}" , userId, role);
        var factory = principalFactoryProvider.getIfAvailable(); // airbng-core 모듈에서 정의한 팩토리 빈
        UserDetails userDetails = factory.apply(claims);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }
}
