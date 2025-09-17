package com.airbng.pay.repository;

import com.airbng.pay.domain.WalletTx;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface WalletTxRepository extends JpaRepository<WalletTx, Long> {
    Optional<WalletTx> findByWalletIdemKey(UUID walletIdemKey);
}
