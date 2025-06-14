package com.airbng.domain;

import com.airbng.domain.base.BaseStatus;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {
    private Long memberId;
    private String email;
    private String name;
    private String phone;
    private String nickname;
    private String password;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;
    private BaseStatus status;
    private Image profileImage;
}
