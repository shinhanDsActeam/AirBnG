package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.image.Image;
import com.airbng.dto.MemberSignupRequest;
import com.airbng.mappers.MemberMapper;
import com.airbng.validator.EmailValidator;
import com.airbng.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberMapper memberMapper;
    @Mock
    private ImageService imageService;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private  EmailValidator emailValidator;
    @Mock
    private  PasswordValidator passwordValidator;

    @InjectMocks
    private MemberServiceImpl memberService;

    @Test
    @DisplayName("이메일 중복 시 예외 발생")
    void signup_duplicateEmail() {
        //given
        MemberSignupRequest dto = new MemberSignupRequest(
                "duplicate@email.com",
                "홍길동",
                "010-1234-5678",
                "nicknameTest",
                "Password1"
        );
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);

        //when
        when(memberMapper.findByEmail(anyString())).thenReturn(true);
        //then
        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.signup(dto, mockFile);
        });
        assertEquals(DUPLICATE_EMAIL, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("이메일 형식 오류")
    void signup_invalidEmail() {
        //given
        MemberSignupRequest dto = new MemberSignupRequest(
                "invalid-email.com",
                "홍길동",
                "010-1234-5678",
                "nicknameTest",
                "Password1"
        );
        MultipartFile mockFile = new MockMultipartFile("file",  new byte[0]);

        //when
        when(memberMapper.findByEmail(dto.getEmail())).thenReturn(false);
        when(memberMapper.findByNickname(dto.getNickname())).thenReturn(false);
        when(memberMapper.findByPhone(dto.getPhone())).thenReturn(false);
        when(passwordValidator.isValidPassword(dto.getPassword())).thenReturn(true);

        //then
        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.signup(dto, mockFile);
        });

        assertEquals(INVALID_EMAIL, exception.getBaseResponseStatus());
    }


    @Test
    @DisplayName("닉네임 중복시 예외 발생")
    void signup_duplicateNickname() {
        //given
        MemberSignupRequest dto = new MemberSignupRequest(
                "duplicate@email.com",
                "홍길동",
                "010-1234-5678",
                "nicknameTest",
                "Password1"
        );
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);

        //when
        when(memberMapper.findByNickname(anyString())).thenReturn(true);

        //then
        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.signup(dto, mockFile);
        });
        assertEquals(DUPLICATE_NICKNAME, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("전화번호 중복시 예외 발생")
    void signup_duplicatePassword() {
        //given
        MemberSignupRequest dto = new MemberSignupRequest(
                "valid@email.com",
                "홍길동",
                "010-1234-5678",
                "nicknameTest",
                "Password1"
        );
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);

        when (memberMapper.findByPhone(anyString())).thenReturn(true);

        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.signup(dto, mockFile);
        });
        assertEquals(DUPLICATE_PHONE, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("비밀번호 형식 예외 발생")
    void signup_invalidPassword() {
        MemberSignupRequest dto = new MemberSignupRequest(
                "valid@email.com",
                "홍길동",
                "010-1234-5678",
                "nicknameTest",
                "Abcdefgh" //비밀번호 형식에 맞지 않게 작성
        );
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);

        when(memberMapper.findByEmail(dto.getEmail())).thenReturn(false);
        when(memberMapper.findByNickname(dto.getNickname())).thenReturn(false);
        when(memberMapper.findByPhone(dto.getPhone())).thenReturn(false);
        when(passwordValidator.isValidPassword(dto.getPassword())).thenReturn(false);

        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.signup(dto, mockFile);
        });
        assertEquals(INVALID_PASSWORD, exception.getBaseResponseStatus());

    }


    @Test
    @DisplayName("정상 회원가입시 insertMember 호출")
    void signup_success() {
        //given
        MemberSignupRequest dto = new MemberSignupRequest(
                "valid@email.com",
                "홍길동",
                "010-1234-5678",
                "nicknameTest",
                "Password1"
        );
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);

        when(memberMapper.findByEmail(dto.getEmail())).thenReturn(false);
        when(memberMapper.findByNickname(dto.getNickname())).thenReturn(false);
        when(memberMapper.findByPhone(dto.getPhone())).thenReturn(false);
        when(passwordValidator.isValidPassword(dto.getPassword())).thenReturn(true);
        when(emailValidator.isValidEmail(dto.getEmail())).thenReturn(true);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(imageService.getDefaultProfileImage()).thenReturn(
                Image.withId(1L)
        );

        //when
        memberService.signup(dto, mockFile);

        //then
        verify(memberMapper).insertMember(ArgumentMatchers.any(Member.class));
    }
}