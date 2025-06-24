package com.airbng.service;

import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.image.Image;
import com.airbng.dto.MemberLoginResponse;
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
import org.springframework.mock.web.MockHttpSession;
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

    @Test
    @DisplayName("로그인 성공 시 MemberLoginResponse 반환")
    void 로그인_성공() {
        // given
        String email = "valid@email.com";
        String password = "Password1";

        Member fakeMember = Member.builder()
                .memberId(10L)
                .email("valid@email.com")
                .name("박재구")
                .phone("010-1111-2222")
                .nickname("재구")
                .password("Password1")
                .status(BaseStatus.ACTIVE)
                .profileImage(Image.withId(1L))  // ✅ Image 객체도 넣어야 함
                .build();

//        Member fakeMember = new Member();
//        fakeMember.setMemberId(10L);
//        fakeMember.setEmail(email);
//        fakeMember.setNickname("재구");

        // when
        when(memberMapper.findByEmailAndPassword(email, password)).thenReturn(fakeMember);

        // then
        MemberLoginResponse response = memberService.login(email, password);
        assertEquals(10L, response.getMemberId());
        assertEquals("valid@email.com", response.getEmail());
        assertEquals("재구", response.getNickname());
    }


    @Test
    @DisplayName("로그인 실패 시 INVALID_MEMBER 예외 발생")
    void 로그인_실패() {
        // given
        String email = "notfound@email.com";
        String password = "wrongPassword";

        // when
        when(memberMapper.findByEmailAndPassword(email, password)).thenReturn(null);

        // then
        MemberException exception = assertThrows(MemberException.class, () -> {
            memberService.login(email, password);
        });
        assertEquals(INVALID_MEMBER, exception.getBaseResponseStatus());
    }

    @Test
    @DisplayName("로그인 시 이메일 형식이 잘못되면 INVALID_EMAIL 예외 발생")
    void 로그인_이메일_형식_오류들_검사() {
        String[] invalidEmails = {
                "user@",
                "@domain.com",
                "user@hi..com",
                "user@domain.com.",
                ".user@domain.com",
                "user@domain_com",
                "user@domain.c",
                "user@domain.abcdefghi",
                " ",
                null
        };

        for (String email : invalidEmails) {
            System.out.println("테스트 중인 이메일: " + email);
            MemberException exception = assertThrows(MemberException.class, () -> {
                memberService.login(email, "Password1234!");
            });
            assertEquals(INVALID_EMAIL, exception.getBaseResponseStatus(), "실패한 이메일: " + email);
        }
    }

    @Test
    @DisplayName("로그인 시 세션에 memberId 저장 확인")
    void 로그인_세션_저장_확인() {
        // given
        String email = "valid@email.com";
        String password = "Password1234!";

        Member member = Member.builder()
                .memberId(42L)
                .email(email)
                .name("재구")
                .nickname("박재구")
                .phone("010-1111-1111")
                .password(password)
                .status(BaseStatus.ACTIVE)
                .profileImage(Image.withId(1L))
                .build();

        when(memberMapper.findByEmailAndPassword(email, password)).thenReturn(member);

        // when
        MemberLoginResponse response = memberService.login(email, password);

        // 세션 객체 생성 및 저장 확인
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("memberId", response.getMemberId());

        Object sessionMemberId = session.getAttribute("memberId");

        // 콘솔 출력
        System.out.println("세션에 저장된 memberId: " + sessionMemberId);

        // then
        assertEquals(42L, session.getAttribute("memberId"));
        assertEquals(email, response.getEmail());
        assertEquals("박재구", response.getNickname());
    }


}