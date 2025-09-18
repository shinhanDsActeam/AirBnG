package com.airbng.consumer.auth;

import com.airbng.consumer.domain.Member;
import com.airbng.consumer.domain.image.Image;
import com.airbng.consumer.repository.ImageRepository;
import com.airbng.consumer.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다."));
        Image image = imageRepository.findById(member.getProfileImage().getImageId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 프로필이 존재하지 않습니다."));
        String profileImageUrl = image.getUrl();
        return new CustomUserDetails(member,profileImageUrl);
    }
}
