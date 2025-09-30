package com.airbng.pay.repository.view;

import com.airbng.pay.domain.view.CompletedReservationView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface CompletedReservationViewRepository extends JpaRepository<CompletedReservationView, Long> {

    @Query("""
      SELECT v FROM CompletedReservationView v
      WHERE v.completedAt >= :from AND v.completedAt < :to
      ORDER BY v.reservationId
    """)
    Slice<CompletedReservationView> findWindow(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
