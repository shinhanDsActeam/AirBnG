package com.airbng.admin.repository;

import com.airbng.admin.domain.PendingLocker;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface LockerReviewRepository extends JpaRepository<PendingLocker, Long> {

    //심사보관소 상세조회
    @Query("select pl from PendingLocker pl " +
            "left join fetch pl.pendingLockerImages pli " +
            "left join fetch pl.pendingLockerJimtypes pj " +
            "where pl.pendingLockerId = :lockerReviewId")
    Optional<PendingLocker> findLockerReviewById(@Param("lockerReviewId") Long lockerReviewId);

}
