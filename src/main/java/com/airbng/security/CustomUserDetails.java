package com.airbng.security;

public class CustomUserDetails {

    @Getter
    public class CustomUserDetails implements UserDetails {

        private Long memberId;
        private String username;  // 보통 이메일이나 아이디
        private String password;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean enabled;

        // 생성자
        public CustomUserDetails(Long memberId, String username, String password,
                                 Collection<? extends GrantedAuthority> authorities, boolean enabled) {
            this.memberId = memberId;
            this.username = username;
            this.password = password;
            this.authorities = authorities;
            this.enabled = enabled;
        }

        // Member 엔티티에서 CustomUserDetails 생성하는 팩토리 메서드
        public static CustomUserDetails fromMember(Member member) {
            return new CustomUserDetails(
                    member.getMemberId(),
                    member.getEmail(),  // username 으로 email 사용
                    member.getPassword(),
                    member.getRoles(),  // GrantedAuthority 리스트 반환 필요
                    true
            );
        }

        // UserDetails 인터페이스 구현 메서드

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public String getUsername() {
            return username;
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;  // 필요에 따라 변경
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;  // 필요에 따라 변경
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;  // 필요에 따라 변경
        }

        @Override
        public boolean isEnabled() {
            return enabled;
        }
    }
}
