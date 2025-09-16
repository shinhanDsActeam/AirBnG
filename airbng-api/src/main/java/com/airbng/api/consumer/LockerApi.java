package com.airbng.api.consumer;

import com.airbng.api.consumer.dto.command.LockerReviewApproveCommand;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;

public interface LockerApi {

    boolean createLockerFromPending(LockerReviewApproveCommand command);
    LockerReviewRejectCommand rejectLockerReview(LockerReviewRejectCommand command);
}
