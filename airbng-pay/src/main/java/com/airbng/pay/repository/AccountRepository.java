package com.airbng.pay.repository;

import com.airbng.pay.domain.Account;
import com.airbng.pay.domain.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByWalletWalletIdAndAccountNumber(Long walletId, String accountNumber);
    boolean existsByWallet(Wallet wallet);
    List<Account> findAllByWalletWalletIdOrderByIsPrimaryDescAccountIdAsc(Long walletId);
    boolean existsByWalletWalletIdAndAccountId(Long walletId, Long accountId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountId = :accountId and a.wallet.walletId = :walletId")
    Optional<Account> findForUpdate(@Param("accountId") Long accountId, @Param("walletId") Long walletId);

    @Lock(LockModeType.OPTIMISTIC)
    @Query("""
        update Account a
        set a.isPrimary = (a.accountId = :newPrimaryId)
        where a.wallet.walletId = :walletId
        and (a.accountId = :newPrimaryId or a.isPrimary = true)
    """)
    @Modifying
    int updatePrimaryAccount(@Param("walletId") Long walletId, @Param("newPrimaryId") Long newPrimaryId);

}
