package com.airbng.admin.service;

import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerTop5Response;

public interface LockerReviewService {
//    public LockerTop5Response findTop5Locker();

    //보관소 심사 상세보기
    LockerReviewDetailResponse findLockerReviewById(Long lockerReviewId);
}
