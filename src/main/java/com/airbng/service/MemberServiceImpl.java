package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.Image;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.dto.MemberSignupRequest;
import com.airbng.mappers.ImageMapper;
import com.airbng.mappers.MemberMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.airbng.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final ImageMapper imageMapper;
    private final ImageService imageService;

    @Transactional
    @Override
    public void signup(MemberSignupRequest dto, MultipartFile file) {
        if (memberMapper.findByEmail(dto.getEmail())) {
            log.info("이메일 중복 오류 발생");
            throw new MemberException(DUPLICATE_EMAIL);
        } else if (memberMapper.findByNickname(dto.getNickname())) {
            log.info("닉네임 중복 오류 발생");
            throw new MemberException(DUPLICATE_NICKNAME);
        } else if (!isValidPassword(dto.getPassword())) {
            log.info("비밀번호 형식 오류 발생");
            throw new MemberException(INVALID_PASSWORD);
        } else if (memberMapper.findByPhone(dto.getPhone())) {
            log.info("휴대폰 번호 오류 발생");
            throw new MemberException(DUPLICATE_PHONE);
        }

        Image profileImage;
        if (file != null && !file.isEmpty()) {
            log.info("이미지 존재");
            profileImage = imageService.uploadProfileImage(file);
        } else {
            log.info("이미지 없음");
            profileImage = imageService.getDefaultProfileImage();
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

    //패스워드 형식 확인
    private boolean isValidPassword(String password) {
        //대문자와 소문자, 하나 이상의 숫자를 포함하여 8자 이상
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }
}
