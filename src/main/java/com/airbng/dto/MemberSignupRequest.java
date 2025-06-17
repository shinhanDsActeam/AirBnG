package com.airbng.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSignupRequest {
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String password;
}
