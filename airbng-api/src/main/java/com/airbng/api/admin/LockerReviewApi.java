package com.airbng.api.admin;

import com.airbng.api.admin.dto.command.LockerReviewCommand;
import com.airbng.api.admin.dto.command.LockerViewStatusCommand;
import com.airbng.api.admin.dto.view.LockerViewStatusView;

// 보관소 심사 전반을 다루는 API 인터페이스
public interface LockerReviewApi {
    // 보관소 심사 요청 메서드
    boolean submitLockerForReview(LockerReviewCommand lockerReviewCommand);

    // 내 보관소 상태 요청 메서드
    LockerViewStatusView getLockerStatusByMemberId(Long memberId);
}
