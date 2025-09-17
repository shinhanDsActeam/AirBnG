package com.airbng.admin.repository;

import com.airbng.admin.domain.PendingLockerImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.awt.*;

public interface LockerReviewImageRepository extends JpaRepository<PendingLockerImage, Long> {
}
