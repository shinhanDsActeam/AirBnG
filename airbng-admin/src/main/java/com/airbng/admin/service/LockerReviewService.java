package com.airbng.admin.service;

import com.airbng.admin.dto.response.LockerReviewDetailResponse;
import com.airbng.api.admin.dto.command.LockerReviewCommand;

public interface LockerReviewService {

    LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId);
}
