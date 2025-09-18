package com.airbng.common.repository;

import com.airbng.common.domain.Aggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AggregateRepository extends JpaRepository<Aggregate, Long> {
    // 보관소별 매출 통계 조회
    @Query("SELECT a " +
            "FROM Aggregate a " +
            "WHERE a.lockerId = :lockerId " +
            "AND a.aggregateDate BETWEEN :startDate AND :endDate")
    Optional<Aggregate> findByStorageSales(Long lockerId, LocalDateTime startDate, LocalDateTime endDate);
}
