package com.airbng.config;

import com.airbng.interceptor.RequestRateLimitInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.airbng")
@EnableTransactionManagement
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestRateLimitInterceptor rateLimitInterceptor;

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        registry.jsp("/WEB-INF/views/", ".jsp");
    }
    // 정적리소스 처리 (스프링이 아니라 톰캣이 처리) 활성화
    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer config) {
        config.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 요청 속도 제한 인터셉터 등록
        registry.addInterceptor(rateLimitInterceptor)
//                .addPathPatterns("/**") // 모든 경로에 적용
                .addPathPatterns("/zzim/**", "/lockers/**")
                .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**"); // Swagger 관련 경로 제외
    }

    //이미지 파일 처리
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(10 * 1024 * 1024); // 10MB
        return resolver;
    }
}
