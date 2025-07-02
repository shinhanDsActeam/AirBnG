package com.airbng.dto;

import lombok.*;

import javax.validation.constraints.Min;


@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberUpdateRequest {
    @NonNull @Min(1)
    private Long memberId;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private Long profileImageId;
}
