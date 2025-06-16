package com.airbng.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class MemberSignupRequestDTO {
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String password;

    private MultipartFile profileImage;
}
