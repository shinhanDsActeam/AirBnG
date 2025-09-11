package com.airbng.core.service;

import com.airbng.core.dto.MemberMyPageResponse;
import com.airbng.core.dto.MemberSignupRequest;
import com.airbng.core.dto.MemberUpdateRequest;
import com.airbng.core.auth.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signup(MemberSignupRequest dto, MultipartFile profileImage);
    void emailCheck(String email);
    MemberMyPageResponse findUserById(Long memberId);
    MemberMyPageResponse updateUserById(MemberUpdateRequest memberUpdateRequest, MultipartFile profileImage, CustomUserDetails userDetails);
    void nicknameCheck(String nickname);
}
