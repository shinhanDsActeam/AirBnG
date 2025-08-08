package com.airbng.repository;

import com.airbng.domain.Reservation;
import com.airbng.domain.base.ReservationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationId(Long reservationId);

    @Query("SELECT COUNT(r) FROM Reservation r " +
            "WHERE (:role = 'KEEPER' AND r.keeperId = :memberId)")
    Long findReservationByMemberIdAndRole(Long memberId, String role);


}
