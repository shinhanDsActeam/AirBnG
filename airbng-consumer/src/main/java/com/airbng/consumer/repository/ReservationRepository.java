package com.airbng.consumer.repository;

import com.airbng.consumer.domain.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
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
            WHERE r.state = 'CONFIRMED'
            AND r.endTime < :deadline AND r.updatedAt < :endTime
            """)
    List<Reservation> findExpiredConfirmedReservations(@Param("deadline") LocalDateTime deadline,
                                                       @Param("endTime") LocalDateTime endTime);

    @Query(""" 
            SELECT  r
            FROM Reservation r
            JOIN FETCH r.dropper d
            JOIN FETCH r.keeper k
            WHERE r.state = 'CONFIRMED'
            AND r.endTime BETWEEN :now AND :deadline
            """)
    List<Reservation> findConfirmedNearEndTime(@Param("now") LocalDateTime now,
                                               @Param("deadline") LocalDateTime deadline);

    /** 결정(승인/거절) 시 동시성 안전을 위한 비관락 */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
           select r
           from Reservation r
           join fetch r.keeper k
           join fetch r.dropper d
           left join fetch r.locker l
           where r.reservationId = :reservationId
           """)
    Optional<Reservation> findByIdForUpdate(@Param("reservationId") Long reservationId);

}
