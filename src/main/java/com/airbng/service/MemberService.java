package com.airbng.service;

import com.airbng.dto.MemberMyPageRequest;
import com.airbng.dto.MemberMyPageResponse;
import com.airbng.domain.Member;
import com.airbng.dto.MemberLoginResponse;
import com.airbng.dto.MemberSignupRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signup(MemberSignupRequest dto, MultipartFile profileImage);
    MemberMyPageResponse findUserById(Long memberId);
    MemberLoginResponse login(String email, String password);
    void emailCheck(String email);
}
