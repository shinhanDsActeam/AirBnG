package com.airbng.api.consumer;

import com.airbng.api.consumer.dto.command.LockerReviewDetailCommand;
import com.airbng.api.consumer.dto.view.LockerReviewDetailView;

public interface LockerApi {
    //보관소 상세보기 요청 메서드
    LockerReviewDetailView getLockerReviewDetail(LockerReviewDetailCommand command);

}
