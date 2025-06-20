package com.airbng.controller;


import com.airbng.common.response.BaseResponse;
import com.airbng.dto.MemberSignupRequest;
import com.airbng.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}