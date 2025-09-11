package com.airbng.core.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MemberLoginRequest {
    private String email;
    private String password;
}
