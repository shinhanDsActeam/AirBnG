package com.airbng.security.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class JwtToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String refreshToken;
    private Date expiration;

    @Builder
    public JwtToken(Long userId, String refreshToken, Date expiration) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiration = expiration;
    }

}
