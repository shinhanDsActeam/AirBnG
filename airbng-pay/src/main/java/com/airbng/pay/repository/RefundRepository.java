package com.airbng.pay.repository;
import com.airbng.pay.domain.Refund;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefundRepository extends JpaRepository<Refund, Long> {
    Optional<Refund> findByBizKey(String bizKey);
}