package com.airbng.api.consumer;

import com.airbng.api.consumer.dto.command.LockerReviewApproveCommand;

public interface AlarmApi {

    // 보관소 승인/반려 알림 메서드
    void sendLockerApproved(Long memberId, String memberName, String lockerName, Long pendingLockerId);
    void sendLockerRejected(Long memberId, String memberName, String lockerName, String reason);


}
