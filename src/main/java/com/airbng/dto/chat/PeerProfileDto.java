package com.airbng.dto.chat;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PeerProfileDto {
    private Long id;           // userId
    private String name;       // 실명
    private String nickname;   // 닉네임
    private String profileUrl; // 프로필 이미지 URL

    public String getDisplayName() {
        return (nickname != null && !nickname.isBlank()) ? nickname
                : (name != null && !name.isBlank()) ? name
                : "상대";
    }
}