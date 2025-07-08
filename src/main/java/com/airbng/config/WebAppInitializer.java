package com.airbng.config;

import com.airbng.common.filter.CachingRequestFilter;
import com.airbng.common.filter.RateLimitFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Slf4j
@Configuration
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        log.info("Starting WebAppInitializer...");

        // 1. DispatcherServlet 설정
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class);

        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/AirBnG/*");
        dispatcher.setAsyncSupported(true);
        log.info("DispatcherServlet async supported enabled.");

        // 2. CachingRequestFilter 등록
        FilterRegistration.Dynamic cachingFilter = servletContext.addFilter("cachingRequestFilter", new CachingRequestFilter());
        cachingFilter.addMappingForUrlPatterns(null, false, "/*");
        cachingFilter.setAsyncSupported(true);
        log.info("CachingRequestFilter async supported enabled.");

        // 3. RateLimitFilter 등록
        FilterRegistration.Dynamic rateLimitFilter = servletContext.addFilter("rateLimitFilter", new RateLimitFilter());
        rateLimitFilter.addMappingForUrlPatterns(null, false, "/*");
        rateLimitFilter.setAsyncSupported(true);
        log.info("RateLimitFilter async supported enabled.");

        log.info("WebAppInitializer completed.");
    }
}
