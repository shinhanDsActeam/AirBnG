package com.airbng.admin.repository;

import com.airbng.admin.domain.LockerReview;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerReviewRepository extends JpaRepository<LockerReview, Long> {
}
