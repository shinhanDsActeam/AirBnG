package com.airbng.admin.repository;

import com.airbng.admin.domain.review.PendingLocker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerReviewJimTypeRepository extends JpaRepository<PendingLocker, Long> {
}
