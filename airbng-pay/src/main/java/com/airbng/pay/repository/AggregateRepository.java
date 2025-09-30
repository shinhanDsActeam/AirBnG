package com.airbng.pay.repository;

import com.airbng.pay.domain.Aggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface AggregateRepository extends JpaRepository<Aggregate, Long> {
    Optional<Aggregate> findByAggregateDateAndLockerId(LocalDateTime aggregateDate, Long lockerId);
}