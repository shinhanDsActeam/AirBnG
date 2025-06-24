package com.airbng.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberMyPageResponse {
    private Long memberId;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private Long profileImageId;
    private String url;
}
