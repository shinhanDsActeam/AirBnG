package com.airbng.common.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
@Order(2)
public class RateLimitFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(httpServletRequest);
            chain.doFilter(cachedRequest, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}