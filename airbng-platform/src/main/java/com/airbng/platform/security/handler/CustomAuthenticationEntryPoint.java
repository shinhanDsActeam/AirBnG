package com.airbng.platform.security.handler;

import com.airbng.platform.common.response.status.BaseResponseStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final SecurityErrorResponder errorResponder;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        log.error("[인증] 인증되지 않는 사용자입니다.");
        BaseResponseStatus status;
        status = BaseResponseStatus.UNAUTHORIZED;
        Object attr = request.getAttribute("JWT_ERROR_STATUS");
        if (attr instanceof BaseResponseStatus s) {
            status = s;
        }
        errorResponder.sendErrorResponse(response, status);
    }
}
