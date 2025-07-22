package com.airbng.repository;

import com.airbng.domain.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("SELECT r FROM Reservation r " +
            "JOIN FETCH r.keeper k " +
            "JOIN FETCH r.dropper d " +
            "JOIN FETCH k.locker kl " +
            "JOIN FETCH d.locker dl " +
            "LEFT JOIN FETCH r.reservationJimTypes rj " +
            "JOIN FETCH rj.jimType j " +
            "WHERE r.reservationId = :reservationId")
    Optional<Reservation> findReservationById(Long reservationId);

}
