package com.airbng.pay.repository;

import com.airbng.pay.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    boolean existsByPayIdemKey(UUID payIdemKey);

    Optional<Payment> findByPayIdemKey(UUID payIdemKey);
}
