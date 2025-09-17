package com.airbng.pay.repository;

import com.airbng.pay.domain.WalletTx;
import com.airbng.pay.domain.WalletTxType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletTxRepository extends JpaRepository<WalletTx, Long> {
    Optional<WalletTx> findByWalletIdemKey(UUID walletIdemKey);

    @Query("""
        select t
        from WalletTx t
        where t.wallet.walletId = :walletId
        and (:type is null or t.walletTxType = :type)
        and (:cursor is null or t.walletTxId < :cursor)
        order by t.walletTxId desc
""")
    List<WalletTx> findSliceByWalletIdAndCursorDesc(@Param("walletId") Long walletId,
                                                    @Param("cursor") Long cursor,
                                                    @Param("type")WalletTxType type,
                                                    Pageable pageable);
}
