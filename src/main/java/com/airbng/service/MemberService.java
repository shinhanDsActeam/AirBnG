package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.dto.MemberSignupRequestDTO;
import com.airbng.mappers.MemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberMapper memberMapper;

    public void signup(MemberSignupRequestDTO dto) {
        Long imageId;

        //프로필 이미지 처리해야함
        if(dto.getProfileImage() !=null && !dto.getProfileImage().isEmpty()) {
        }

        Member member = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .phone(dto.getPhone())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .status(BaseStatus.ACTIVE)
                .profileImage()
                .profileImageId
    }
}
