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
//    @NonNull
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

    //   테스트용
    public static Member withId(Long memberId) {
        return Member.builder()
                .memberId(memberId)
                .email("owner@airbng.com")
                .name("홍길동")
                .phone("010-1234-5678")
                .nickname("lockerKing")
                .password("encoded_password")
                .status(BaseStatus.ACTIVE)
                .profileImage(Image.withId(101L))
                .build();
    }

}
