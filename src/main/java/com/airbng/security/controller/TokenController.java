package com.airbng.security.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.security.dto.TokenResponse;
import com.airbng.security.service.JwtService;
import com.airbng.security.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.airbng.security.util.JwtUtil.*;

@RestController
@RequiredArgsConstructor
public class TokenController {

    private final JwtService  jwtService;
    private final JwtUtil jwtUtil;

    @PostMapping("/reissue")
    public BaseResponse<String> reissue(HttpServletRequest request, HttpServletResponse response) {
        TokenResponse newTokens = jwtService.reissueToken(request);

        response.setHeader(TOKEN_HEADER, TOKEN_PREFIX + newTokens.getAccessToken());
        response.addHeader("Set-Cookie", jwtUtil.createCookie(TOKEN_TYPE_REFRESH, newTokens.getRefreshToken()).toString());
        response.addHeader("Set-Cookie", jwtUtil.createSseCookie(TOKEN_TYPE_SSE, newTokens.getAccessToken()).toString());
        return new BaseResponse<>("토큰이 재발급 되었습니다.");
    }
}
