package com.airbng.controller;


import com.airbng.common.response.BaseResponse;
import com.airbng.dto.*;
import com.airbng.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.servlet.http.HttpSession;

import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS;
import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS_LOGIN;

@RestController
@RequestMapping("/members")
@RequiredArgsConstructor
@Slf4j
@Validated
public class MemberController {

    private final MemberService memberService;

    @PostMapping(value = "/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<String> signup(
            @RequestPart("profile") MemberSignupRequest dto,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {
        memberService.signup(dto, profileImage);
        return new BaseResponse<>("회원가입 성공");
    }

    @GetMapping("/check-email")
    public BaseResponse<String> emailCheck(@RequestParam String email) {
        memberService.emailCheck(email);
        return new BaseResponse<>("사용 가능한 이메일");
    }

    @GetMapping("/check-nickname")
    public BaseResponse<String> nicknameCheck(@RequestParam String nickname) {
        memberService.nicknameCheck(nickname);
        return new BaseResponse<>("사용 가능한 닉네임");
    }

    @GetMapping
    public BaseResponse<MemberMyPageResponse> findUserById(
            @RequestParam @NotNull @Min(1) Long memberId
    ) {
        MemberMyPageRequest request = MemberMyPageRequest.builder()
                .memberId(memberId)
                .build();

        MemberMyPageResponse response = memberService.findUserById(request.getMemberId());
        return new BaseResponse<>(response);
    }

    @PostMapping("/login")
    public BaseResponse<MemberLoginResponse> login(@RequestBody MemberLoginRequest request,
                                                   HttpSession session) {
        log.info("로그인 요청: email={}, password={}", request.getEmail(), request.getPassword());

        MemberLoginResponse response = memberService.login(request.getEmail(), request.getPassword());

        log.info("로그인 결과: {}", response);

        // 세션에 memberId 저장
        session.setAttribute("memberId", response.getMemberId());
        session.setAttribute(("nickname"), response.getNickname());

        return new BaseResponse<>(SUCCESS_LOGIN, response);
    }

}