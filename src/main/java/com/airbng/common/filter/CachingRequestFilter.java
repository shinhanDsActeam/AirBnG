package com.airbng.common.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 에러 로그를 남기기 위해 request를 캐싱해두는 필터<br>
 * inputStream을 읽으면 request body가 소진되기 때문에<br>
 * RequestWrapper를 사용하여 request body를 캐싱
 */
@Component
@Order(1)
public class CachingRequestFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        if (request instanceof HttpServletRequest) {
            System.out.println("[Filter] CachedBodyHttpServletRequest 적용됨");

            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            chain.doFilter(cachedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
