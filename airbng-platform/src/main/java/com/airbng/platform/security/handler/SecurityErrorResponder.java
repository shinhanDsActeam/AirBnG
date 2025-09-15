package com.airbng.platform.security.handler;

import com.airbng.platform.common.response.status.ResponseStatus;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface SecurityErrorResponder {
    void sendErrorResponse(HttpServletResponse response, ResponseStatus status) throws IOException;
}
