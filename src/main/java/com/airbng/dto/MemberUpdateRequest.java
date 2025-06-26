package com.airbng.dto;

import lombok.*;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {
    private Long memberId;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private Long profileImageId;
}
