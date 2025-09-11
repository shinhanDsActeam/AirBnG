package com.airbng.dto;

import com.airbng.domain.Member;
import jakarta.validation.constraints.Min;
import lombok.*;


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


    public static MemberMyPageResponse from(Member member) {
        return MemberMyPageResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .profileImageId(member.getProfileImage() != null ? member.getProfileImage().getImageId() : null)
                .url(member.getProfileImage() != null ? member.getProfileImage().getUrl() : null)
                .build();
    }
}
