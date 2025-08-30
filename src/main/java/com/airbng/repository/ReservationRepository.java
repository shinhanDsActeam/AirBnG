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
            SELECT r.reservationId, r.state, r.startTime, r.endTime,
                d.memberId AS dropper_id,
                d.email AS dropper_email,
                d.name AS dropper_name,
                d.phone AS dropper_phone,
                d.nickname AS dropper_nickname,
                d.password AS dropper_password,
                d.status AS dropper_status,
            
                k.memberId AS keeper_id,
                k.email AS keeper_email,
                k.name AS keeper_name,
                k.phone AS keeper_phone,
                k.nickname AS keeper_nickname,
                k.password AS keeper_password,
                k.status AS keeper_status
            FROM Reservation r
            LEFT JOIN r.dropper d
            left join r.keeper k
            WHERE r.state = 'CONFIRMED' AND r.endTime < :deadline
            AND r.updatedAt < :updatedAt
            """)
    List<ReservationResponse> findExpiredConfirmedReservations(@Param("deadline") LocalDateTime deadline,
                                                               @Param("updatedAt") LocalDateTime updatedAt);

    @Query(""" 
            SELECT r.reservationId, r.state, r.startTime, r.endTime,
                d.memberId AS dropper_id,
                d.email AS dropper_email,
                d.name AS dropper_name,
                d.phone AS dropper_phone,
                d.nickname AS dropper_nickname,
                d.password AS dropper_password,
                d.status AS dropper_status,
            
                k.memberId AS keeper_id,
                k.email AS keeper_email,
                k.name AS keeper_name,
                k.phone AS keeper_phone,
                k.nickname AS keeper_nickname,
                k.password AS keeper_password,
                k.status AS keeper_status
            FROM Reservation r
            left join r.dropper d
            left join r.keeper k
            WHERE r.state = 'CONFIRMED' AND FUNCTION('TIMESTAMPDIFF', MINUTE, :now, r.endTime) BETWEEN 0 AND 30
            """)
    List<ReservationResponse> findConfirmedNearEndTime(@Param("now") LocalDateTime now);

}
