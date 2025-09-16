package com.airbng.api.consumer;

import com.airbng.api.consumer.dto.command.LockerReviewMemberCommand;
import com.airbng.api.consumer.dto.view.LockerReviewMemberView;

public interface LockerApi {

    //보관소 keeper 이름 요청
    LockerReviewMemberView getLockerReviewList(LockerReviewMemberCommand command);
}
