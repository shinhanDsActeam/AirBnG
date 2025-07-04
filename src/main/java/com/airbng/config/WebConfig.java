package com.airbng.config;

import com.airbng.interceptor.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;

import java.util.List;

@Configuration
@EnableWebMvc
@EnableAsync                // 비동기 메서드 실행 지원
@EnableScheduling           // 스케줄러 활성화
@ComponentScan(basePackages = "com.airbng")
@EnableTransactionManagement
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final RequestRateLimitInterceptor rateLimitInterceptor;
    private final RedisRateLimitInterceptor redisRateLimitInterceptor;
    private final LoginRateLimitInterceptor loginRateLimitInterceptor;

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
        // 기존 메모리 기반 인터셉터
        registry.addInterceptor(rateLimitInterceptor)
                .addPathPatterns("/lockers/**/members/**/zzim")
                .excludePathPatterns("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs");

        // Redis 기반 인터셉터 추가 등록
        // TODO : 나중에 다시 구현
        registry.addInterceptor(redisRateLimitInterceptor)
                .addPathPatterns("/members/todo/**") // Path 수정 가능, 예: "/lockers/**"
                .excludePathPatterns("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs");

        registry.addInterceptor(loginRateLimitInterceptor)
                .addPathPatterns("/**/members/login")
                .excludePathPatterns("/swagger-ui.html", "/swagger-resources/**", "/v2/api-docs");
    }

    //이미지 파일 처리
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        resolver.setMaxUploadSize(10 * 1024 * 1024); // 10MB
        return resolver;
    }

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
