package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.image.Image;
import com.airbng.dto.*;
import com.airbng.mappers.ImageMapper;
import com.airbng.mappers.MemberMapper;
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
public class MemberServiceImpl implements MemberService {

    private final MemberMapper memberMapper;
    private final ImageMapper imageMapper;
    private final ImageService imageService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;

    @Transactional
    @Override
    public void signup(MemberSignupRequest dto, MultipartFile file) {
        //예외 처리
        if (memberMapper.findByEmail(dto.getEmail()))               throw new MemberException(DUPLICATE_EMAIL);
        if (memberMapper.findByNickname(dto.getNickname()))         throw new MemberException(DUPLICATE_NICKNAME);
        if (memberMapper.findByPhone(dto.getPhone()))               throw new MemberException(DUPLICATE_PHONE);
        if (!passwordValidator.isValidPassword(dto.getPassword()))  throw new MemberException(INVALID_PASSWORD);
        if (!emailValidator.isValidEmail(dto.getEmail()))           throw new MemberException(INVALID_EMAIL);


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
                .status(BaseStatus.ACTIVE)
                .profileImage(profileImage)
                .build();
        memberMapper.insertMember(member);
    }

    //이메일 중복 검사
    @Override
    public void emailCheck(String email) {
        if (memberMapper.findByEmail(email))               throw new MemberException(DUPLICATE_EMAIL);
        if (!emailValidator.isValidEmail(email))           throw new MemberException(INVALID_EMAIL);
    }

    @Override
    public MemberLoginResponse login(String email, String password) {
        if (!emailValidator.isValidEmail(email)) {
            throw new MemberException(INVALID_EMAIL);
        }

        try {
            Member member = memberMapper.findByEmailAndPassword(email, password);

            log.info("Member id found: {}", member.getMemberId());

            return MemberLoginResponse.from(member);
        } catch (NullPointerException e) {
            throw new MemberException(INVALID_MEMBER);
        }
    }

    public void nicknameCheck(String nickname) {
        if (memberMapper.findByNickname(nickname))          throw new MemberException(DUPLICATE_NICKNAME);
    }


    @Override
    public MemberMyPageResponse findUserById(Long memberId) {
        //Long memberId = request.getMemberId();

        MemberMyPageResponse response = memberMapper.findUserById(memberId);

        if (response == null) throw new MemberException(NOT_FOUND_MEMBER);

        return  MemberMyPageResponse.builder()
                .memberId(response.getMemberId())
                .email(response.getEmail())
                .name(response.getName())
                .phone(response.getPhone())
                .nickname(response.getNickname())
                .profileImageId(response.getProfileImageId())
                .url(response.getUrl())
                .build();

    }

    @Transactional
    @Override
    public MemberMyPageResponse updateUserById(MemberUpdateRequest request, MultipartFile profileImage) {
        MemberMyPageResponse existing = memberMapper.findUserById(request.getMemberId());

        if (existing == null) throw new MemberException(NOT_FOUND_MEMBER);
        if (memberMapper.findByEmail(request.getEmail()))               throw new MemberException(DUPLICATE_EMAIL);
        if (memberMapper.findByNickname(request.getNickname()))         throw new MemberException(DUPLICATE_NICKNAME);
        if (memberMapper.findByPhone(request.getPhone()))               throw new MemberException(DUPLICATE_PHONE);
        if (request.getEmail() == null || request.getEmail().isEmpty()) request.setEmail(existing.getEmail());

        if (!emailValidator.isValidEmail(request.getEmail()))           throw new MemberException(INVALID_EMAIL);
        if (request.getName() == null) request.setName(existing.getName());
        if (request.getPhone() == null) request.setPhone(existing.getPhone());
        if (request.getNickname() == null) request.setNickname(existing.getNickname());

        Image image = (profileImage != null && !profileImage.isEmpty())
                ? imageService.uploadProfileImage(profileImage)
                : imageService.updateDefaultProfileImage(profileImage, request.getMemberId());

        request.setProfileImageId(image.getImageId());

        int updateCount = memberMapper.updateUserById(request);

        if (updateCount == 0) throw new MemberException(NOT_UPDATE_MEMBER);

        return memberMapper.findUserById(request.getMemberId());
    }
}
