package com.airbng.admin.service;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.dto.response.LockerReviewListResponse;
import org.springframework.data.domain.Page;

public interface LockerReviewService {

    Page<LockerReviewListResponse> findAllByReviewStatus(ReviewStatus status, int page);

}
