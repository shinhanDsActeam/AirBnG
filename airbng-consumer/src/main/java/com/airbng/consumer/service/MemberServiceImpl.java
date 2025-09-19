package com.airbng.consumer.service;

import com.airbng.api.pay.PayApi;
import com.airbng.api.pay.WalletApi;
import com.airbng.api.pay.dto.command.WalletCreateCommand;
import com.airbng.api.pay.dto.view.WalletInfoView;
import com.airbng.consumer.auth.CustomUserDetails;
import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.base.Role;
import com.airbng.consumer.dto.MemberMyPageResponse;
import com.airbng.consumer.dto.MemberSignupRequest;
import com.airbng.consumer.repository.MemberRepository;
import com.airbng.consumer.validator.EmailValidator;
import com.airbng.consumer.validator.PasswordValidator;
import com.airbng.consumer.exception.MemberException;
import com.airbng.common.base.BaseStatus;
import com.airbng.consumer.domain.image.Image;
import com.airbng.consumer.dto.MemberUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.airbng.platform.common.response.status.BaseResponseStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class MemberServiceImpl implements MemberService {

//    private final MemberMapper memberMapper;
    private final ImageService imageService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final EmailValidator emailValidator;
    private final PasswordValidator passwordValidator;
    private final MemberRepository memberRepository;
    private final PayApi payApi;
    private final WalletApi walletApi;

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

        payApi.createWallet(new WalletCreateCommand(member.getMemberId()));
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
    public MemberMyPageResponse getMyPageInfoById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        WalletInfoView view = walletApi.getWalletInfo(memberId);
        return MemberMyPageResponse.of(member, view);
    }

    @Transactional
    @Override
    public MemberMyPageResponse updateUserById(MemberUpdateRequest request, MultipartFile profileImage, CustomUserDetails userDetails) {
        Long userId = userDetails.getId();
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        if (!userId.equals(request.getMemberId())) {
            throw new MemberException(FAILED_UPDATE_MEMBER);
        }

        String phone = request.getPhone().replace("-", "");

        // 닉네임 중복 체크
        if (!member.getNickname().equals(request.getNickname()) &&
                memberRepository.existsByNickname(request.getNickname())) {
            throw new MemberException(DUPLICATE_NICKNAME);
        }
        // 휴대폰 중복 체크
        if (!member.getPhone().equals(phone) &&
                memberRepository.existsByPhone(phone)) {
            throw new MemberException(DUPLICATE_PHONE);
        }

        Image image;
        if (profileImage != null && !profileImage.isEmpty()) {
            image = imageService.updateProfileImage(profileImage, userId);
        } else {
            image = (member.getProfileImage() != null)
                    ? member.getProfileImage()
                    : imageService.getDefaultProfileImage();
        }

        member.updateInfo(member.getEmail(), request.getName(), request.getPhone(), request.getNickname(), image);
        return MemberMyPageResponse.from(member);
    }
}
