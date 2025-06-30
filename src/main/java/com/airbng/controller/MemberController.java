package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.MemberLoginRequest;
import com.airbng.dto.MemberLoginResponse;
import com.airbng.dto.MemberSignupRequest;
import com.airbng.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS_LOGIN;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> signup(
            @RequestPart("profile") MemberSignupRequest dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        memberService.signup(dto, profileImage);
        return new BaseResponse<>("회원가입 성공");
    }

    @PostMapping("/check-email")
    public ResponseEntity<BaseResponse<String>> emailCheck(@RequestParam String email) {
        boolean exists = memberService.emailCheck(email);
        String message = exists ? "이미 사용 중인 이메일" : "사용 가능한 이메일";
        return ResponseEntity.ok(new BaseResponse<>(message));
    }

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponse> login(@RequestBody MemberLoginRequest request,
                                                   HttpSession session,
                                                   HttpServletRequest httpRequest) {
        log.info("Controller 도달");

        // Interceptor에서 참조 가능하게 세팅
        httpRequest.setAttribute("loginEmail", request.getEmail());
        log.info("로그인 요청: email={}, password={}", request.getEmail(), request.getPassword());

        MemberLoginResponse response = memberService.login(request.getEmail(), request.getPassword());
        log.info("로그인 결과: {}", response);

        session.setAttribute("memberId", response.getMemberId());
        return new BaseResponse<>(SUCCESS_LOGIN, response);
    }

}