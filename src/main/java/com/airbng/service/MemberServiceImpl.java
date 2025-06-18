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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void signup(MemberSignupRequest dto, MultipartFile file) {
        //예외 처리
        if (memberMapper.findByEmail(dto.getEmail()))      throw new MemberException(DUPLICATE_EMAIL);
        if (memberMapper.findByNickname(dto.getNickname()))throw new MemberException(DUPLICATE_NICKNAME);
        if (memberMapper.findByPhone(dto.getPhone()))      throw new MemberException(DUPLICATE_PHONE);
        if (!isValidPassword(dto.getPassword()))             throw new MemberException(INVALID_PASSWORD);

        //이미지 처리
        Image profileImage = (file != null && !file.isEmpty())
                ? imageService.uploadProfileImage(file)
                : imageService.getDefaultProfileImage();

        //패스워드 암호화
        String encodedPw = passwordEncoder.encode(dto.getPassword());

        Member member = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .phone(dto.getPhone())
                .nickname(dto.getNickname())
                .password(encodedPw)
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
