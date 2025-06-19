package com.airbng.service;

import com.airbng.dto.MemberSignupRequest;
import org.springframework.web.multipart.MultipartFile;

public interface MemberService {
    void signup(MemberSignupRequest dto, MultipartFile profileImage);
    boolean emailCheck(String email);
}
