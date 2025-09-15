package com.airbng.platform.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    public static final String DEFAULT_FILTER_PROCESSES_URL = "/login";
    public static final String DEFAULT_FILTER_HTTP_METHOD = "POST";
    public static final String CONTENT_TYPE = "application/json";
    public static final String ENCODING = "UTF-8";
    public static final String SPRING_SECURITY_USERNAME_KEY = "email";
    public static final String SPRING_SECURITY_PASSWORD_KEY = "password";
    private static final AntPathRequestMatcher DEFAULT_LOGIN_PATH_REQUEST_MATCHER =
            new AntPathRequestMatcher(DEFAULT_FILTER_PROCESSES_URL, DEFAULT_FILTER_HTTP_METHOD);

    private final ObjectMapper objectMapper;

    public CustomAuthenticationFilter(ObjectMapper objectMapper) {
        super(DEFAULT_LOGIN_PATH_REQUEST_MATCHER);
        this.objectMapper = objectMapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if(request.getContentType() == null || !request.getContentType().equals(CONTENT_TYPE)) {
            throw new AuthenticationServiceException("로그인 요청을 불러오는 중 오류가 발생했습니다.");
        }

        String messageBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);

        Map<String, String> loginAttempt = objectMapper.readValue(messageBody, Map.class);
        String username = loginAttempt.get(SPRING_SECURITY_USERNAME_KEY);
        String password = loginAttempt.get(SPRING_SECURITY_PASSWORD_KEY);
        log.info("[로그인 요청] : {}", username);
        log.info("[로그인 요청] : {}", password);

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password);
        return this.getAuthenticationManager().authenticate(authToken);
    }
}
