package com.airbng.repository;

import com.airbng.domain.Locker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LockerRepository extends JpaRepository<Locker, Long> {

    @Query("SELECT l FROM Locker l " +
            "JOIN FETCH l.lockerImages li " +
            "JOIN FETCH l.lockerJimTypes lj " +
            "WHERE l.lockerId = :lockerId")
    Optional<Locker> findLockerById(Long lockerId);

}
