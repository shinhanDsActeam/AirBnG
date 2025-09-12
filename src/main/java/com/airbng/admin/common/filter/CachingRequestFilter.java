package com.airbng.admin.common.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;
import java.util.Locale;

/**
 * 에러 로그를 남기기 위해 request를 캐싱해두는 필터<br>
 * inputStream을 읽으면 request body가 소진되기 때문에<br>
 * RequestWrapper를 사용하여 request body를 캐싱
 */

/**
 * @RequestPart는 서블릿의 멀티파트 파서가 바디를 온전히 보존하고 있어야 파트를 읽을 수 있음
 * 아래 필터들이 바디를 먼저 읽거나 헤더/스트림 위임이 완전하지 않아서 파싱이 깨지는 문제가 발생
 * 따라서 멀티파트는 그대로 통과시키게 수정
 */
@Component
@Order(1)
public class CachingRequestFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest httpReq)) {
            chain.doFilter(request,response);
            return;
        }
        //멀티파트는 스킵
        if (isMultipart(httpReq)) {
            chain.doFilter(request,response);
            return;
        }
        if (request instanceof HttpServletRequest) {
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
            chain.doFilter(wrappedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
    private boolean isMultipart(HttpServletRequest req) {
        String ct = req.getContentType();
        return ct != null && ct.toLowerCase(Locale.ROOT).startsWith("multipart/");
    }
}