//package com.airbng.config;
//
//import com.airbng.common.filter.CachingRequestFilter;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.WebApplicationInitializer;
//
//import javax.servlet.FilterRegistration;
//import javax.servlet.ServletContext;
//import javax.servlet.ServletException;
//
//@Configuration
//public class WebAppInitializer implements WebApplicationInitializer {
//    @Override
//    public void onStartup(ServletContext servletContext) {
//        FilterRegistration.Dynamic cachingFilter = servletContext.addFilter("cachingRequestFilter", new CachingRequestFilter());
//        cachingFilter.addMappingForUrlPatterns(null, false, "/*");
//    }
//}

package com.airbng.config;

import com.airbng.common.filter.CachingRequestFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

@Configuration
public class WebAppInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        // 1. DispatcherServlet 등록 및 async 지원 활성화
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(WebConfig.class); // 기존 WebConfig 등록


        DispatcherServlet dispatcherServlet = new DispatcherServlet(context);
        ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcherServlet", dispatcherServlet);
        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/AirBnG/*"); // URL 매핑 설정
        System.out.println("Starting WebAppInitializer...");
        dispatcher.setAsyncSupported(true);  // async 지원 활성화
        System.out.println("DispatcherServlet async supported enabled.");


        // 2. 필터 등록 및 async 지원 활성화
        FilterRegistration.Dynamic cachingFilter = servletContext.addFilter("cachingRequestFilter", new CachingRequestFilter());
        cachingFilter.addMappingForUrlPatterns(null, false, "/*");
        cachingFilter.setAsyncSupported(true);  // async 지원 활성화

        System.out.println("CachingRequestFilter async supported enabled.");
        System.out.println("WebAppInitializer completed.");
    }
}
