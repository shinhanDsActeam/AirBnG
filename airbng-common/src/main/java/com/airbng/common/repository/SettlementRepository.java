package com.airbng.common.repository;

import com.airbng.common.domain.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    // 기간별 매출 통계 조회
    @Query("SELECT s " +
            "FROM Settlement s " +
            "WHERE s.settlementDate BETWEEN :startDate AND :endDate")
    Optional<Settlement> findByPeriodSales(LocalDateTime startDate, LocalDateTime endDate);
}
