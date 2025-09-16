package com.airbng.pay.repository;

import com.airbng.pay.domain.BankInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BankInfoRepository extends JpaRepository<BankInfo, Long> {
    boolean existsByBankCode(Long bankCode);

    BankInfo findByBankCode(Long bankCode);
}
