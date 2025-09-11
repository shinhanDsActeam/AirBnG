package com.airbng.security.handler;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.common.response.status.ResponseStatus;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final SecurityErrorResponder errorResponder;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        BaseResponseStatus status;
        if (exception instanceof BadCredentialsException) {
            log.error("[인증] 잘못된 이메일 혹은 비밀번호");
            status = BaseResponseStatus.INVALID_USERNAME_OR_PASSWORD;
        } else {
            log.error("[인증] 인증 실패");
            status = BaseResponseStatus.FAILURE;
        }
        errorResponder.sendErrorResponse(response, status);
    }
}
