package com.airbng.admin.repository;

import com.airbng.admin.domain.review.PendingLocker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerReviewImageRepository extends JpaRepository<PendingLocker, Long> {
}
