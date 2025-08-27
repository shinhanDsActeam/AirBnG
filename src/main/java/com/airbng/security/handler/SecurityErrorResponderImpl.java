package com.airbng.security.handler;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.ResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class SecurityErrorResponderImpl implements SecurityErrorResponder {

    private final ObjectMapper objectMapper;

    @Override
    public void sendErrorResponse(HttpServletResponse response, ResponseStatus status) throws IOException {
        BaseResponse<Void> body = new BaseResponse<>(status);
        response.setStatus(status.getHttpStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
