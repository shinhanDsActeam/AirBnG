package com.airbng.pay.repository;

import com.airbng.pay.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByMemberId(Long memberId);
}