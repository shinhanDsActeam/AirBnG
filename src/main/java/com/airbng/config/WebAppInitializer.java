package com.airbng.config;

import com.airbng.common.filter.CachingRequestFilter;
import com.airbng.common.filter.RateLimitFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

@Configuration
public class WebAppInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) {

        FilterRegistration.Dynamic rateLimitFilter = servletContext.addFilter("rateLimitFilter", new RateLimitFilter());
        rateLimitFilter.addMappingForUrlPatterns(null, false, "/*");

        FilterRegistration.Dynamic cachingFilter = servletContext.addFilter("cachingRequestFilter", new CachingRequestFilter());
        cachingFilter.addMappingForUrlPatterns(null, false, "/*");

    }
}