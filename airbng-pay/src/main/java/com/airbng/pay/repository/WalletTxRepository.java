package com.airbng.pay.repository;

import com.airbng.pay.domain.WalletTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletTxRepository extends JpaRepository<WalletTx, Long> {

}
