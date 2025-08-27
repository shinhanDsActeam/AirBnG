package com.airbng.security.repository;

import com.airbng.security.domain.JwtToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface JwtTokenRepository extends JpaRepository<JwtToken, Long> {
    Boolean existsByRefreshToken(String username);

    @Transactional
    void deleteByRefreshToken(String refreshToken);
}
