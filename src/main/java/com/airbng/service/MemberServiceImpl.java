package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.image.Image;
import com.airbng.dto.MemberMyPageResult;
import com.airbng.dto.MemberLoginResponse;
import com.airbng.dto.MemberSignupRequest;
import com.airbng.mappers.MemberMapper;
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
    private final ImageService imageService;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public void signup(MemberSignupRequest dto, MultipartFile file) {
        //예외 처리
        if (memberMapper.findByEmail(dto.getEmail())) throw new MemberException(DUPLICATE_EMAIL);
        if (memberMapper.findByNickname(dto.getNickname())) throw new MemberException(DUPLICATE_NICKNAME);
        if (memberMapper.findByPhone(dto.getPhone())) throw new MemberException(DUPLICATE_PHONE);
        if (!isValidPassword(dto.getPassword())) throw new MemberException(INVALID_PASSWORD);
        if (!isValidEmail(dto.getEmail())) throw new MemberException(INVALID_EMAIL);


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
    public boolean emailCheck(String email) {
        return memberMapper.findByEmail(email);
    }

    //패스워드 형식 확인
    private boolean isValidPassword(String password) {
        //대문자와 소문자, 하나 이상의 숫자를 포함하여 8자 이상
        return password != null && password.matches("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}$");
    }

    //이메일 형식 체크
    private boolean isValidEmail(String email) {
        //@앞의 문자 1개이상, @ 뒤에 문자+ . + 2~6자(com, net, co,kr , email)
        // 점(.)이 연속되거나, @ 앞/뒤 형식이 잘못된 경우 모두 차단
        return email != null && email.matches("^[A-Za-z0-9]+([._%+-]?[A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,6}$");
    }

    @Override
    public MemberMyPageResult findUserById(Long memberId) {
        MemberMyPageResult result = memberMapper.findUserById(memberId);

        if (result == null) throw new MemberException(MEMBER_NOT_FOUND);

        MemberMyPageResult response =  MemberMyPageResult.builder()
                .memberId(result.getMemberId())
                .email(result.getEmail())
                .name(result.getName())
                .phone(result.getPhone())
                .nickname(result.getNickname())
                .profileImageId(result.getProfileImageId())
                .url(result.getUrl())
                .build();

        return response;
    }
}
    public MemberLoginResponse login(String email, String password) {
        if (!isValidEmail(email)) {
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

}
