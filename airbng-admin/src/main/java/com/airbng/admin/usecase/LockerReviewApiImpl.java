package com.airbng.admin.usecase;

import com.airbng.api.admin.LockerReviewApi;
import com.airbng.api.admin.dto.command.LockerReviewCommand;
import org.springframework.stereotype.Service;

@Service
class LockerReviewApiImpl implements LockerReviewApi {

    @Override
    public boolean submitLockerForReview(LockerReviewCommand lockerReviewCommand) {
        return false;
    }
}
