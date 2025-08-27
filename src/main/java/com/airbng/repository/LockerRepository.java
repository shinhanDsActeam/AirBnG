package com.airbng.repository;

import com.airbng.domain.Locker;
import com.airbng.domain.base.ReservationState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    @Query("SELECT l FROM Locker l " +
            "JOIN FETCH l.keeper k " +
            "LEFT JOIN FETCH l.lockerImages li " +
            "LEFT JOIN FETCH li.image i " +
            "LEFT JOIN FETCH l.lockerJimTypes lj " +
            "LEFT JOIN FETCH lj.jimType j " +
            "WHERE l.lockerId = :lockerId")
    Optional<Locker> findLockerById(Long lockerId);

    @Query("SELECT DISTINCT l FROM Locker l " +
            "JOIN FETCH l.keeper k " +
            "LEFT JOIN FETCH l.lockerImages li " +
            "LEFT JOIN FETCH li.image i " +
            "LEFT JOIN FETCH l.lockerJimTypes lj " +
            "JOIN FETCH lj.jimType j " +
            "ORDER BY l.reservationCount DESC " +
            "limit 5")
    List<Locker> findTop5LockersByReservation(ReservationState state);
}
