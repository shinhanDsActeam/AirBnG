package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.base.Role;
import com.airbng.domain.image.Image;
import com.airbng.dto.*;
import com.airbng.mappers.ImageMapper;
import com.airbng.mappers.MemberMapper;
import com.airbng.repository.MemberRepository;
import com.airbng.validator.EmailValidator;
import com.airbng.validator.PasswordValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import static com.airbng.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final ImageService imageService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void signup(MemberSignupRequest dto, MultipartFile file) {
        //예외 처리
        if (memberRepository.existsByEmail(dto.getEmail())) throw new MemberException(DUPLICATE_EMAIL);
        if (memberRepository.existsByNickname(dto.getNickname())) throw new MemberException(DUPLICATE_NICKNAME);
        if (memberRepository.existsByPhone(dto.getPhone())) throw new MemberException(DUPLICATE_PHONE);
        if (!passwordValidator.isValidPassword(dto.getPassword())) throw new MemberException(INVALID_PASSWORD);
        if (!emailValidator.isValidEmail(dto.getEmail())) throw new MemberException(INVALID_EMAIL);


        //이미지 처리
        Image profileImage = (file != null && !file.isEmpty())
                ? imageService.uploadProfileImage(file)
                : imageService.getDefaultProfileImage();

        String encodedPw = passwordEncoder.encode(dto.getPassword());

        Member member = Member.builder()
                .email(dto.getEmail())
                .name(dto.getName())
                .phone(dto.getPhone())
                .nickname(dto.getNickname())
                .password(encodedPw)
                .role(Role.USER)
                .status(BaseStatus.ACTIVE)
                .profileImage(profileImage)
                .build();
        memberRepository.save(member);
    }

    //이메일 중복 검사
    @Override
    public void emailCheck(String email) {
        if (memberRepository.existsByEmail(email))         throw new MemberException(DUPLICATE_EMAIL);
        if (!emailValidator.isValidEmail(email))           throw new MemberException(INVALID_EMAIL);
    }


    public void nicknameCheck(String nickname) {
        if (memberRepository.existsByNickname(nickname)) throw new MemberException(DUPLICATE_NICKNAME);
    }

    @Override
    public MemberMyPageResponse findUserById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        return MemberMyPageResponse.from(member);
    }

    @Transactional
    @Override
    public MemberMyPageResponse updateUserById(MemberUpdateRequest request, MultipartFile profileImage) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        // 이메일 검증 및 중복 체크 (기존 이메일과 다른 경우에만)
        if (!emailValidator.isValidEmail(request.getEmail())) {
            throw new MemberException(INVALID_EMAIL);
        }
        if (!member.getMemberId().equals(request.getMemberId()) &&
                memberRepository.existsByEmail(request.getEmail())) {
            throw new MemberException(DUPLICATE_EMAIL);
        }

        // 닉네임 중복 체크
        if(!member.getMemberId().equals(request.getMemberId()) &&
                memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberException(DUPLICATE_NICKNAME);
        }

        // 휴대폰 중복 체크
        if(!member.getMemberId().equals(request.getMemberId()) &&
                memberRepository.existsByPhone(request.getPhone())) {
            throw new MemberException(DUPLICATE_PHONE);
        }

        Image image = (profileImage != null && !profileImage.isEmpty())
                ? imageService.uploadProfileImage(profileImage)
                : imageService.updateDefaultProfileImage(profileImage, request.getMemberId());

        member.updateInfo(request.getEmail(), request.getName(), request.getPhone(), request.getNickname(), image);

        return MemberMyPageResponse.from(member);
    }
}
