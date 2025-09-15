package com.airbng.admin.repository;

import com.airbng.admin.domain.PendingLocker;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.*;
import java.util.Optional;

public interface LockerReviewRepository extends JpaRepository<PendingLocker, Long> {


}
