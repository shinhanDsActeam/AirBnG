package com.airbng.consumer.repository;

import com.airbng.consumer.domain.image.LockerImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LockerImageRepository extends JpaRepository<LockerImage, Long> {}
