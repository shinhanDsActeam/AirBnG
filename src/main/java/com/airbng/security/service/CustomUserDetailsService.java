package com.airbng.security.service;

import com.airbng.domain.Member;
import com.airbng.repository.MemberRepository;
import com.airbng.security.domain.CustomUserDetails;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다."));
        return new CustomUserDetails(member);
    }

    public UserDetails loadUserById(Long memberId) throws UsernameNotFoundException {
        // id가 memberId라면 findById로 충분. 아니면 findByMemberId 사용
        Member m = memberRepository.findById(memberId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 회원이 존재하지 않습니다."));
        return new CustomUserDetails(m);
    }
}
