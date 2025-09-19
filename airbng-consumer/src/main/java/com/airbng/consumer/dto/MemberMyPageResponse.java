package com.airbng.consumer.dto;

import com.airbng.api.pay.dto.view.WalletInfoView;
import com.airbng.consumer.domain.Member;
import lombok.*;

import java.math.BigDecimal;


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
    private Long walletId;
    private BigDecimal balance;


    public static MemberMyPageResponse of(Member member, WalletInfoView view) {
        return MemberMyPageResponse.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .name(member.getName())
                .phone(member.getPhone())
                .nickname(member.getNickname())
                .profileImageId(member.getProfileImage() != null ? member.getProfileImage().getImageId() : null)
                .url(member.getProfileImage() != null ? member.getProfileImage().getUrl() : null)
                .walletId(view.getWalletId())
                .balance(view.getBalance())
                .build();
    }

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
