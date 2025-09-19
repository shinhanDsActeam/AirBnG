package com.airbng.api.consumer;

import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;
import com.airbng.api.consumer.dto.command.LockerReviewMemberCommand;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;

import com.airbng.api.consumer.dto.command.LockerReviewApproveCommand;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;

public interface LockerApi {
    //보관소 상세보기 요청 메서드
    LockerReviewDetailView getLockerReviewDetail(LockerReviewDetailCommand command);


    //보관소 keeper 이름 요청
    LockerReviewMemberView getLockerReviewList(LockerReviewMemberCommand command);

    boolean createLockerFromPending(LockerReviewApproveCommand command);
    boolean rejectLockerReview(LockerReviewRejectCommand command);
}
