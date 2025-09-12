package com.airbng.platform.security.handler;

import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.security.principal.AirbngPrincipal;
import com.airbng.platform.security.service.JwtService;
import com.airbng.platform.security.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.util.*;

import static com.airbng.platform.security.filter.CustomAuthenticationFilter.CONTENT_TYPE;
import static com.airbng.platform.security.filter.CustomAuthenticationFilter.ENCODING;
import static com.airbng.platform.security.util.JwtUtil.*;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    private final ObjectMapper objectMapper =  new ObjectMapper();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        Long userId = null;
        String nickname = null;
        Collection<? extends GrantedAuthority> authorities = List.of();

        if (principal instanceof AirbngPrincipal ap) {
            userId = ap.getId();
            nickname = ap.getNickname();
            authorities = ap.getAuthorities();
        }

        log.info("[CustomAuthenticationSuccessHandler] userId: {}, nickname: {}", userId, nickname);
        log.info("[CustomAuthenticationSuccessHandler] authorities: {}", authorities);


        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.next().getAuthority();

        //토큰 생성
        log.info("[JWT] 토큰 발급");
        String accessToken = jwtUtil.createJwtToken(TOKEN_TYPE_ACCESS, userId, role, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = jwtUtil.createJwtToken(TOKEN_TYPE_REFRESH, userId, role, REFRESH_TOKEN_EXPIRATION);

        //refresh Token 저장
        jwtService.addRefreshToken(userId, refreshToken, REFRESH_TOKEN_EXPIRATION);

        response.addHeader(TOKEN_HEADER, TOKEN_PREFIX + accessToken);
        response.addHeader("Set-Cookie", jwtUtil.createCookie(TOKEN_TYPE_REFRESH, refreshToken).toString());

        Map<String, Object> responseData = new HashMap<>();
        responseData.put("memberId", userId);
        responseData.put("role", role);
        responseData.put("nickname", nickname);

        BaseResponse<Map<String, Object>> body = new BaseResponse<>(responseData);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(CONTENT_TYPE);
        response.setCharacterEncoding(ENCODING);
        response.getWriter().write(objectMapper.writeValueAsString(body));

        log.info("[로그인 성공] userId = {}", userId);
    }
}
