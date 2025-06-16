package com.airbng.domain;

import com.airbng.domain.base.BaseStatus;

import com.airbng.domain.base.BaseTime;
import com.airbng.domain.image.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.lang.NonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member extends BaseTime {
    @NonNull
    private Long memberId;
    @NonNull
    private String email;
    @NonNull
    private String name;
    @NonNull
    private String phone;
    @NonNull
    private String nickname;
    @NonNull
    private String password;
    @NonNull
    private BaseStatus status;
    @NonNull
    private Image profileImage;
}
