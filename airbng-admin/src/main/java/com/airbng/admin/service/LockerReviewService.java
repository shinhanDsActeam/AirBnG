package com.airbng.admin.service;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.dto.response.LockerReviewListResponse;
import org.springframework.data.domain.Page;
import com.airbng.admin.dto.response.LockerReviewDetailResponse;
import com.airbng.api.admin.dto.command.LockerReviewCommand;

public interface LockerReviewService {

    Page<LockerReviewListResponse> findAllByReviewStatus(ReviewStatus status, int page);
  
   LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId);
}