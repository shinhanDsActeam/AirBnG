package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.*;
import com.airbng.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS_LOGIN;
import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS_LOGOUT;

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

    @GetMapping("/my-page/{memberId}")
    public BaseResponse<MemberMyPageResponse> findUserById(
            @PathVariable("memberId") @NotNull @Min(1) Long memberId
    ) {
        MemberMyPageRequest request = MemberMyPageRequest.builder()
                .memberId(memberId)
                .build();

        MemberMyPageResponse response = memberService.findUserById(request.getMemberId());
        return new BaseResponse<>(response);
    }

    @PostMapping("/my-page/update")
    public BaseResponse<MemberMyPageResponse> updateUserById(
            @Valid @RequestPart ("memberUpdateRequest") MemberUpdateRequest memberUpdateRequest,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            HttpSession session) {

        log.info("회원 정보 수정 요청: {}", memberUpdateRequest);
        log.info("프로필 이미지: {}", profileImage != null ? profileImage.getOriginalFilename() : "없음");

        MemberMyPageResponse response = memberService.updateUserById(memberUpdateRequest, profileImage);
        session.setAttribute("memberId", response.getMemberId());
        session.setAttribute("nickname", response.getNickname());
        return new BaseResponse<>(response);
    }
}