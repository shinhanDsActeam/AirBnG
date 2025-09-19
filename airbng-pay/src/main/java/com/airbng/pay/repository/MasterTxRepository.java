package com.airbng.pay.repository;

import com.airbng.pay.domain.MasterTx;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MasterTxRepository extends JpaRepository<MasterTx, Long> {
}
