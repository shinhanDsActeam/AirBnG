package com.airbng.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginRequest {
    private String email;
    private String password;
}
