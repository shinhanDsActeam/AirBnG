package com.airbng.service;

import com.airbng.dto.*;
import com.airbng.domain.Member;
import com.airbng.security.domain.CustomUserDetails;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signup(MemberSignupRequest dto, MultipartFile profileImage);
    void emailCheck(String email);
    MemberMyPageResponse findUserById(Long memberId);
    MemberMyPageResponse updateUserById(MemberUpdateRequest memberUpdateRequest, MultipartFile profileImage, CustomUserDetails userDetails);
    void nicknameCheck(String nickname);
}
