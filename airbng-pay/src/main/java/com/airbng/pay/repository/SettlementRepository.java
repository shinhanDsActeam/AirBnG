package com.airbng.pay.repository;

import com.airbng.pay.domain.Settlement;
import com.airbng.pay.dto.AggByLocker;
import com.airbng.pay.dto.AggTotal;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

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

    boolean existsByReservationId(Long reservationId);

    //  보관소별 합계
    @Query("""
              select new com.airbng.pay.dto.AggByLocker(
              s.lockerId,
                coalesce(sum(s.amount), 0),
                count(s),
                coalesce(sum(s.paymentFee), 0)
                )
              from Settlement s
              where s.settlementDate >= :from and s.settlementDate < :to
              group by s.lockerId
            """)
    List<AggByLocker> aggregateByLocker(@Param("from") LocalDateTime from,
                                        @Param("to") LocalDateTime to);

    // 전체 합계
    @Query("""
            select 
            new com.airbng.pay.dto.AggTotal(
            coalesce(sum(s.amount), 0), count(s), coalesce(sum(s.paymentFee), 0)
            )
            from Settlement s
            where s.settlementDate >= :from and s.settlementDate < :to
        """)
    AggTotal aggregateTotal(@Param("from") LocalDateTime from,
                            @Param("to") LocalDateTime to);
}
