package com.airbng.admin.repository;

import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.domain.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LockerReviewRepository extends JpaRepository<PendingLocker, Long> {

    Optional<Reservation> findByLockerReviewId(Long lockerReviewId);


    // 상세 조회 (N+1 방지용 fetch join)
    @EntityGraph(attributePaths = {"lockerReview", "memberId", "lockerImages.image", "lockerJimtypes.jimType"})
    @Query("select pl from PendingLocker pl join fetch pl.lockerReview lr where lr.lockerReviewId = :lockerReviewId")
    Optional<PendingLocker> findLockerReviewById(@Param("lockerReviewId") Long lockerReviewId);


    //목록+페이징
    Page<PendingLocker> findAll(Pageable pageable);

}
