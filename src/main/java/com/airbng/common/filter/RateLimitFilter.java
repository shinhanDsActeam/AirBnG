package com.airbng.common.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(2)
public class RateLimitFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest httpServletRequest) {
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            chain.doFilter(cachedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}