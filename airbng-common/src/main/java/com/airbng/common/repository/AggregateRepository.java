package com.airbng.common.repository;

import com.airbng.common.domain.Aggregate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AggregateRepository extends JpaRepository<Aggregate, Long> {
}
