package com.airbng.repository;

import com.airbng.domain.Locker;
import com.airbng.domain.base.ReservationState;
import com.airbng.dto.locker.LockerPreviewResult;
import com.airbng.dto.locker.LockerSearchRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT l FROM Locker l " +
            "JOIN FETCH l.keeper k " +
            "LEFT JOIN FETCH l.lockerImages li " +
            "LEFT JOIN FETCH li.image i " +
            "LEFT JOIN FETCH l.lockerJimTypes lj " +
            "LEFT JOIN FETCH lj.jimType j " +
            "WHERE k.memberId = :memberId")
    Optional<Locker> findLockerByMemberId(Long memberId);

    @Query("SELECT DISTINCT l FROM Locker l " +
            "JOIN FETCH l.keeper k " +
            "LEFT JOIN FETCH l.lockerImages li " +
            "LEFT JOIN FETCH li.image i " +
            "LEFT JOIN FETCH l.lockerJimTypes lj " +
            "LEFT JOIN FETCH lj.jimType j " +
            "WHERE (:address IS NULL OR :address = '' OR l.address LIKE CONCAT('%', :address, '%'))" +
            "AND (:lockerName IS NULL OR :lockerName = '' OR l.lockerName LIKE CONCAT('%', :lockerName, '%'))" +
            "AND ((:jimTypeIds) IS NULL OR j.jimTypeId IN (:jimTypeIds))" +
            "ORDER BY l.lockerId")
    List<Locker> findAllLockerBySearch(@Param("address") String address,
                                       @Param("lockerName") String lockerName,
                                       @Param("jimTypeIds") List<Long> jimTypeIds);

    @Query("SELECT COUNT(DISTINCT l.lockerId)" +
            "FROM Locker l " +
            "LEFT JOIN l.lockerJimTypes lj " +
            "LEFT JOIN lj.jimType j " +
            "WHERE (:address IS NULL OR :address = '' OR l.address LIKE CONCAT('%', :address, '%'))" +
            "AND (:lockerName IS NULL OR :lockerName = '' OR l.lockerName LIKE CONCAT('%', :lockerName, '%'))" +
            "AND ((:jimTypeIds) IS NULL OR j.jimTypeId IN (:jimTypeIds))")
    Long findLockerCount(LockerSearchRequest condition);
}
