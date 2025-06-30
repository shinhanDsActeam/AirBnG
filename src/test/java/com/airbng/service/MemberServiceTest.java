package com.airbng.service;

import com.airbng.common.exception.ImageException;
import com.airbng.common.exception.MemberException;
import com.airbng.domain.Member;
import com.airbng.domain.base.BaseStatus;
import com.airbng.domain.image.Image;
import com.airbng.dto.*;
import com.airbng.mappers.MemberMapper;
import com.airbng.util.S3Utils;
import com.airbng.validator.EmailValidator;
import com.airbng.validator.PasswordValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import static com.airbng.common.response.status.BaseResponseStatus.*;
import static org.apache.commons.io.FilenameUtils.getExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {
    @Mock
    private MemberMapper memberMapper;

    @Mock
    private S3Utils s3Utils;

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

    @Nested
    @DisplayName("회원 정보 조회 테스트")
    class FindUserByIdTest {
        @Test
        @DisplayName("회원 정보 조회 정상 반환 테스트")
        void 회원정보_조회_정상_반환() {
//            MemberMyPageRequest request = new MemberMyPageRequest();
//            request.setMemberId(1L);

            Long memberId = 1L; // 테스트용 회원 ID

            MemberMyPageResponse response = MemberMyPageResponse.builder()
                    .memberId(1L)
                    .email("a@airbng.com")
                    .name("회원11")
                    .phone("01011111111")
                    .nickname("회원11")
                    .profileImageId(1L)
                    .url("https://cdn.airbng.com/image11.jpg")
                    .build();

            Mockito.when(memberMapper.findUserById(memberId))
                    .thenReturn(response);

            MemberMyPageResponse result = memberService.findUserById(memberId);

            assertEquals(memberId, result.getMemberId());
            assertEquals("a@airbng.com", response.getEmail());
        }

        @Nested
        @DisplayName("회원 정보 조회 예외 처리 테스트")
        class FindUserByIdExceptionTest {
            @Test
            @DisplayName("회원 정보 조회 실패 시 예외 메시지 확인 테스트")
            void 회원정보_조회_예외_메시지_확인() {
                //MemberMyPageRequest request = new MemberMyPageRequest();

                Long memberId = 999L; // 존재하지 않는 회원 ID

                Mockito.when(memberMapper.findUserById(memberId))
                        .thenReturn(null);

                MemberException exception = assertThrows(MemberException.class, () -> {
                    memberService.findUserById(memberId);
                });

                assertEquals(NOT_FOUND_MEMBER, exception.getBaseResponseStatus()); // 예외 내부에 errorCode 필드가 있다면
            }
        }
    }

    @Test
    @DisplayName("정상 입력 시 회원 정보가 수정되고 수정된 정보 반환 테스트")
    void 회원정보_수정_성공() {
        Long memberId = 1L;
        String url = "https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/b2362d14-1793-41a7-" +
        "8ba1-bbbcfb96724f_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89" +
                "%E1%85%A3%E1%86%BA%202025-06-23%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%209.35.14.png";

        // 수정할 회원 정보
        MemberUpdateRequest request = new MemberUpdateRequest(
                memberId,
                "a1@airbng.com",
                "회원1",
                "0101010101",
                "회원1",
                41L
        );
        MultipartFile mockFile = new MockMultipartFile("file", new byte[0]);
        Image updatedImage = Image.withId(41L);

        // 이메일 형식 검증 및 이미지 서비스 호출에 대한 Mock 설정
        when(memberMapper.findByEmail(request.getEmail())).thenReturn(false);
        when(memberMapper.findByNickname(request.getNickname())).thenReturn(false);
        when(memberMapper.findByPhone(request.getPhone())).thenReturn(false);
        // 이메일 검증됐다고 가정
        when(emailValidator.isValidEmail(request.getEmail())).thenReturn(true);
        when(imageService.updateDefaultProfileImage(eq(mockFile), eq(memberId))).thenReturn(updatedImage);

        // 예상되는 응답 객체 생성
        MemberMyPageResponse expectedResponse = MemberMyPageResponse.builder()
                .memberId(memberId)
                .email("a1@airbng.com")
                .name("회원1")
                .phone("0101010101")
                .nickname("회원1")
                .profileImageId(41L)
                .url(url)
                .build();

        when(memberMapper.findUserById(memberId)).thenReturn(expectedResponse);
        when(memberMapper.updateUserById(any(MemberUpdateRequest.class))).thenReturn(1);
        MemberMyPageResponse result = memberService.updateUserById(request, mockFile);

        // 결과 검증
        assertEquals("a1@airbng.com", result.getEmail());
        assertEquals("회원1", result.getName());
        assertEquals("0101010101", result.getPhone());
        assertEquals("회원1", result.getNickname());
        assertEquals(41L, result.getProfileImageId());
        assertEquals(url, result.getUrl());
    }

    @Nested
    @DisplayName("이미지 업로드 관련 예외 처리 테스트")
    class ImageUploadExceptionTest {
        @Test
        @DisplayName("이미지 파일을 업로드를 했지만 이미지 파일이 비어서 들어가 실패한 경우")
        void 회원정보_수정_이미지_업로드_실패() {
            Long memberId = 1L;
            String url = "https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/b2362d14-1793-41a7-" +
                    "8ba1-bbbcfb96724f_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89" +
                    "%E1%85%A3%E1%86%BA%202025-06-23%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%209.35.14.png";
            MultipartFile emptyFile = new MockMultipartFile("file", "image.png", "image/png", new byte[0]);

            // 수정할 회원 정보
            MemberUpdateRequest request = new MemberUpdateRequest(
                    memberId,
                    "a@airbng.com",
                    "회원1",
                    "0101010101",
                    "회원1",
                    41L
            );

            when(memberMapper.findUserById(memberId)).thenReturn(
                    MemberMyPageResponse.builder()
                            .memberId(memberId)
                            .email("a@airbng.com")
                            .name("회원1")
                            .phone("0101010101")
                            .nickname("회원1")
                            .profileImageId(41L)
                            .url("https://dummyurl.com")
                            .build()
            );

            when(emailValidator.isValidEmail(anyString())).thenReturn(true);
            when(imageService.updateDefaultProfileImage(eq(emptyFile), eq(memberId)))
                    .thenThrow(new IllegalArgumentException("업로드할 파일이 비어 있습니다."));

            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                    memberService.updateUserById(request, emptyFile)
            );

            assertEquals("업로드할 파일이 비어 있습니다.", exception.getMessage());
        }

        @Test
        @DisplayName("이미지가 아닌 확장자를 업로드 했을 때 예외 발생")
        void 회원정보_수정_이미지_확장자_오류() {
            Long memberId = 1L;
            String url = "https://airbngbucket.s3.ap-northeast-2.amazonaws.com/profiles/b2362d14-1793-41a7-" +
                    "8ba1-bbbcfb96724f_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89" +
                    "%E1%85%A3%E1%86%BA%202025-06-23%20%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE%209.35.14.png";

            MultipartFile invalidFile = new MockMultipartFile(
                    "file",
                    "malicious.exe",
                    "application/octet-stream",
                    new byte[0]
            );

            MemberUpdateRequest request = new MemberUpdateRequest(
                    memberId,
                    "a1@airbng.com",
                    "회원1",
                    "0101111",
                    "회원1",
                    41L
            );

            when(memberMapper.findUserById(memberId)).thenReturn(
                    MemberMyPageResponse.builder()
                            .memberId(memberId)
                            .email("a1@airbng.com")
                            .name("회원1")
                            .phone("0101111")
                            .nickname("회원1")
                            .profileImageId(41L)
                            .url(url)
                            .build()
            );

            when(emailValidator.isValidEmail(anyString())).thenReturn(true);

            // 이미지 서비스에서 확장자 오류 예외 던짐
            when(imageService.updateDefaultProfileImage(any(MultipartFile.class), eq(memberId)))
                    .thenThrow(new ImageException(INVALID_EXTENSIONS));

            ImageException exception = assertThrows(ImageException.class, () ->
                    memberService.updateUserById(request, invalidFile)
            );

            assertEquals("허용되지 않는 파일 확장자입니다.", exception.getMessage());
        }

        @Test
        @DisplayName("이미지 용량 초과 시 예외 발생")
        void 회원정보_수정_이미지_용량_초과_오류() {
            Long memberId = 1L;
            String url = "https://m.blog.naver.com/playbazzi/221094082882?view=img_1";

            byte[] largeImageBytes = new byte[10 * 1024 * 1024]; // 10MB 이미지
            MultipartFile largeImageFile = new MockMultipartFile(
                    "file",
                    "largeImage.png",
                    "image/png",
                    largeImageBytes
            );

            MemberUpdateRequest request = new MemberUpdateRequest(
                    memberId,
                    "a1@airbng.com",
                    "회원1",
                    "0101111",
                    "회원1",
                    41L
            );

            when(memberMapper.findUserById(memberId)).thenReturn(
                    MemberMyPageResponse.builder()
                            .memberId(memberId)
                            .email("a1@airbng.com")
                            .name("회원1")
                            .phone("0101111")
                            .nickname("회원1")
                            .profileImageId(41L)
                            .url(url)
                            .build()
            );

            when(emailValidator.isValidEmail(anyString())).thenReturn(true);

            // 이미지 용량 초과 시 예외 발생하도록 설정
            when(imageService.uploadProfileImage(any(MultipartFile.class)))
                    .thenThrow(new ImageException(EXCEED_FILE_SIZE));

            ImageException exception = assertThrows(ImageException.class, () ->
                    memberService.updateUserById(request, largeImageFile)
            );

            assertEquals("이미지 크기가 초과되었습니다. 최대 10MB까지 업로드 가능합니다.", exception.getMessage());
        }
    }



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