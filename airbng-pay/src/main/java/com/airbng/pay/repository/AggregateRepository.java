package com.airbng.pay.repository;

import com.airbng.pay.domain.Aggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregateRepository extends JpaRepository<Aggregate, Long> {
}