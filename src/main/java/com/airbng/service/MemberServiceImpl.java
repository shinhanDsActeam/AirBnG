package com.airbng.service;

import com.airbng.domain.Image;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.dto.MemberSignupRequest;
import com.airbng.mappers.ImageMapper;
import com.airbng.mappers.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final ImageMapper imageMapper;
    private final ImageService imageService;

    private static final String DEFAULT_PROFILE_URL ="https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/users/default.jpeg";
    private static final String DEFAULT_PROFILE_NAME = "default.jpeg";
    @Override
    public void signup(MemberSignupRequest dto, MultipartFile file) {
        Image profileImage;
        try {
            if (file != null && !file.isEmpty()) {
                log.info("이미지 존재함~~~~~~");
                profileImage = imageService.uploadProfileImage(file);
            } else {
                log.info("이미지 없음~~~~~~");
                profileImage = imageService.getDefaultProfileImage();
                log.info("이미지 정보 확인 " + profileImage.getImageId() + "          "  + profileImage.getUrl());
            }
        } catch (IOException e) {
            throw new RuntimeException("프로필 이미지 업로드 중 오류 발생", e);
        }

        Member member = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .phone(dto.getPhone())
                .nickname(dto.getNickname())
                .password(dto.getPassword())
                .status(BaseStatus.ACTIVE)
                .profileImage(profileImage)
                .build();
        memberMapper.insertMember(member);
    }
}
