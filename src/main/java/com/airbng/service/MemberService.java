package com.airbng.service;

import com.airbng.dto.*;
import com.airbng.domain.Member;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signup(MemberSignupRequest dto, MultipartFile profileImage);
    MemberLoginResponse login(String email, String password);
    void emailCheck(String email);
    MemberMyPageResponse findUserById(Long memberId);
    MemberMyPageResponse updateUserById(MemberUpdateRequest memberUpdateRequest, MultipartFile profileImage);
}
