package com.airbng.consumer.service;

import com.airbng.consumer.dto.MemberMyPageResponse;
import com.airbng.consumer.dto.MemberSignupRequest;
import com.airbng.consumer.dto.MemberUpdateRequest;
import com.airbng.consumer.auth.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signup(MemberSignupRequest dto, MultipartFile profileImage);
    void emailCheck(String email);
    MemberMyPageResponse findUserById(Long memberId);
    MemberMyPageResponse updateUserById(MemberUpdateRequest memberUpdateRequest, MultipartFile profileImage, CustomUserDetails userDetails);
    void nicknameCheck(String nickname);
}
