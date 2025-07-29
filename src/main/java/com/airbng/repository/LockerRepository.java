package com.airbng.repository;

import com.airbng.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LockerRepository extends JpaRepository<Locker, Long> {

    @Query("SELECT l FROM Locker l " +
            "JOIN FETCH l.keeper k " +
            "LEFT JOIN FETCH l.lockerImages li " +
            "JOIN FETCH li.image i " +
            "LEFT JOIN FETCH l.lockerJimTypes lj " +
            "JOIN FETCH lj.jimType j " +
            "WHERE l.lockerId = :lockerId")
    Optional<Locker> findLockerById(Long lockerId);

    @Query("SELECT l.keeper.memberId FROM Locker l " +
            "WHERE l.lockerId = :lockerId" )
    Optional<Long> getKeeperIdByLockerId(Long lockerId);


}
