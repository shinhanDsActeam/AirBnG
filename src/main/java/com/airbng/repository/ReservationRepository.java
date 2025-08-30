package com.airbng.repository;

import com.airbng.domain.Reservation;
import com.airbng.dto.reservation.ReservationResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Optional<Reservation> findByReservationId(Long reservationId);

    @Query("SELECT DISTINCT r FROM Reservation r " +
            "JOIN FETCH r.dropper d " +
            "JOIN FETCH r.keeper k " +
            "JOIN FETCH r.locker l " +
            "LEFT JOIN FETCH l.lockerImages li " +
            "LEFT JOIN FETCH li.image i " +
            "LEFT JOIN FETCH r.reservationJimTypes rj " +
            "LEFT JOIN FETCH rj.jimType jt " +
            "WHERE r.reservationId = :reservationId")
    Optional<Reservation> findReservationDetailById(Long reservationId);


    @Query(""" 
            SELECT  r
            FROM Reservation r
            JOIN FETCH r.dropper d
            JOIN FETCH r.keeper k
            WHERE r.state = 'CONFIRMED' AND r.endTime < :deadline
            AND r.updatedAt < :updatedAt
            """)
    List<Reservation> findExpiredConfirmedReservations(@Param("deadline") LocalDateTime deadline,
                                                               @Param("updatedAt") LocalDateTime updatedAt);

    @Query(""" 
            SELECT  r
            FROM Reservation r
            JOIN FETCH r.dropper d
            JOIN FETCH r.keeper k
            WHERE r.state = 'CONFIRMED' AND FUNCTION('TIMESTAMPDIFF', MINUTE, :now, r.endTime) BETWEEN 0 AND 30
            """)
    List<Reservation> findConfirmedNearEndTime(@Param("now") LocalDateTime now);

}
