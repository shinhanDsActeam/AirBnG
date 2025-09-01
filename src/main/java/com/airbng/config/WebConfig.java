package com.airbng.config;

import com.airbng.common.filter.CachingRequestFilter;
import com.airbng.common.filter.RateLimitFilter;
import com.airbng.interceptor.LoginRateLimitInterceptor;
import com.airbng.interceptor.RedisRateLimitInterceptor;
import com.airbng.interceptor.RequestRateLimitInterceptor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

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
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST")
                .allowCredentials(true);
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

    @Bean
    public MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }

    // LocalDateTime 처리
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public FilterRegistrationBean<CachingRequestFilter> cachingRequestFilterBean() {
        final FilterRegistrationBean<CachingRequestFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new CachingRequestFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setAsyncSupported(true);

        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterBean() {
        final FilterRegistrationBean<RateLimitFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RateLimitFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        registrationBean.setAsyncSupported(true);

        return registrationBean;
    }
}
