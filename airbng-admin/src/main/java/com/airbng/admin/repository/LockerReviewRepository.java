package com.airbng.admin.repository;

import com.airbng.admin.domain.LockerReview;
import com.airbng.admin.domain.PendingLocker;
import com.airbng.admin.domain.base.ReviewStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerReviewRepository extends JpaRepository<LockerReview, Long> {

    // 상태별 페이징 조회
    Page<LockerReview> findAllByReviewStatus(ReviewStatus status, Pageable pageable);
}
