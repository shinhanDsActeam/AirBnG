package com.airbng.pay.repository;

import com.airbng.pay.domain.Settlement;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Long> {
    // 기간별 매출 통계 조회
    @Query("SELECT s " +
            "FROM Settlement s " +
            "WHERE s.settlementDate BETWEEN :startDate AND :endDate " +
            "ORDER BY s.settlementDate DESC")
    Page<Settlement> findByPeriodSales(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
