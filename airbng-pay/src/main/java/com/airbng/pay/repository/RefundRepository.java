package com.airbng.pay.repository;

import com.airbng.pay.domain.Refund;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByBizKey(UUID bizKey);

    @Query("""
              SELECT r FROM Refund r
              WHERE r.refundStatus = 'REQUESTED'
                AND r.createdAt >= :from AND r.createdAt < :to
              ORDER BY r.refundId
            """)
    Slice<Refund> findWindow(LocalDateTime from, LocalDateTime to, Pageable pageable);

}