package com.airbng.util;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void writeErrorResponse(HttpServletResponse response, BaseResponseStatus status) throws IOException {
        response.setStatus(status.getHttpStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BaseResponse<Object> errorResponse = new BaseResponse<>(status);
        String json = objectMapper.writeValueAsString(errorResponse);

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }

    public static void writeSuccessResponse(HttpServletResponse response, BaseResponseStatus status) throws IOException {
        response.setStatus(status.getHttpStatus());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BaseResponse<Object> successResponse = new BaseResponse<>(status);
        String json = objectMapper.writeValueAsString(successResponse);

        PrintWriter writer = response.getWriter();
        writer.write(json);
        writer.flush();
    }
}