package com.airbng.security.filter;

import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Member;
import com.airbng.security.domain.CustomUserDetails;
import com.airbng.security.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.airbng.security.util.JwtUtil.*;

@Slf4j
@RequiredArgsConstructor
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

            filterChain.doFilter(request, response);
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
        } catch (Exception e) {
            log.error("[JWT 필터] 토큰 검증 실패 : 인증 실패");
            request.setAttribute("JWT_ERROR_CODE", BaseResponseStatus.FAILURE);
            throw new InsufficientAuthenticationException("인증실패");
        }
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

    private void setAuthentication(String token) {
        Long userId = jwtUtil.getUserId(token);
        String role = jwtUtil.getRole(token);
        log.info("[JWT 필터] 토큰 검증 성공 - ID: {}, 역할: {}" , userId, role);
        CustomUserDetails userDetails = new CustomUserDetails(new Member(userId, role));
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities())
        );
    }
}
