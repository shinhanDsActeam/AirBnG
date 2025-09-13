package com.airbng.admin.usecase;

import com.airbng.admin.api.LockerReviewApi;
import com.airbng.admin.api.dto.command.LockerReviewCommand;
import org.springframework.stereotype.Service;

@Service
class LockerReviewApiImpl implements LockerReviewApi {

    @Override
    public boolean submitLockerForReview(LockerReviewCommand lockerReviewCommand) {
        // create pending locker
        return false;
    }
}
