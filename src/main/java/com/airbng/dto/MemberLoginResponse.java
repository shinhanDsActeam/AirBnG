package com.airbng.dto;

import com.airbng.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MemberLoginResponse {
    private Long memberId;
    private String nickname;
    private String email;

    public static MemberLoginResponse from(Member member) {
        return MemberLoginResponse.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .build();
    }
}
