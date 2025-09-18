package com.airbng.pay.repository;

import com.airbng.pay.domain.Wallet;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, Long> {

    // 데드락 방지 - memberId 오름차순 정렬로 락 획득
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select w from Wallet w where w.memberId in :memberIds order by w.walletId asc")
    List<Wallet> findWalletsWithLockByMemberIds(@Param("memberIds") Collection<Long> memberIds);

    Optional<Wallet> findByMemberId(Long memberId);
}