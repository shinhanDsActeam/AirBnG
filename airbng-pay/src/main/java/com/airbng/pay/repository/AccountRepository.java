package com.airbng.pay.repository;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByWalletWalletIdAndAccountNumber(Long walletId, String accountNumber);
    boolean existsByWallet(Wallet wallet);
}
