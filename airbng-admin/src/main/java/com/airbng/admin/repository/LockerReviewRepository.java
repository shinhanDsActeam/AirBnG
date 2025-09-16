package com.airbng.admin.repository;

import com.airbng.admin.domain.PendingLocker;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

public interface LockerReviewRepository extends JpaRepository<PendingLocker, Long> {

    // 상세 조회 (waiting 상태인 리뷰만)
    @EntityGraph(attributePaths = {"lockerReview", "pendingLockerImages", "pendingLockerJimtypes"})
    @Query("select pl from PendingLocker pl join pl.lockerReview lr where lr.lockerReviewId = :lockerReviewId")
    Optional<PendingLocker> findLockerReviewById(@Param("lockerReviewId") Long lockerReviewId);

}
