package com.airbng.admin.repository;

import com.airbng.domain.Locker;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LockerReviewRepository {

    // 상세 조회 (N+1 방지용 fetch join)
    @EntityGraph(attributePaths = { "keeper", "lockerImages.image", "lockerJimTypes.jimType" })
    @Query("select l from PendingLocker pl left join LockerReview lr where pl.lockerReviewId = :lockerReviewId")
    Optional<Locker> findLockerReviewById(@Param("lockerReviewId") Long lockerReviewId);

}
