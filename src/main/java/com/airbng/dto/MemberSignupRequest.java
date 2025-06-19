package com.airbng.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MemberSignupRequest {
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String password;
}
