package com.airbng.core.auth;

import com.airbng.core.domain.Member;
import com.airbng.core.domain.base.BaseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Member member;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new ArrayList<>();
        collection.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return member.getRole().toString();
            }
        });
        return collection;
    }

    public Long getId() {return member.getMemberId(); }

    public String getNickname() {return member.getNickname(); }

    @Override
    public String getUsername() {return member.getEmail(); }

    @Override
    public String getPassword() {return member.getPassword(); }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    //Active가 아닐경우 로그인 실패
    @Override
    public boolean isEnabled() { return member.getStatus() == BaseStatus.ACTIVE; }
}
