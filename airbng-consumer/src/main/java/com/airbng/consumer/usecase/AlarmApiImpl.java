package com.airbng.consumer.usecase;

import com.airbng.api.consumer.AlarmApi;
import com.airbng.api.consumer.LockerApi;
import com.airbng.consumer.scheduler.AlertScheduledTask;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlarmApiImpl implements AlarmApi {

    private final AlertScheduledTask alertScheduledTask;

    @Override
    @Transactional
    public void sendLockerApproved(Long memberId, String memberName, String lockerName, Long pendingLockerId) {
        // 실제 SSE 발송
        alertScheduledTask.sendLockerApproved(memberId, memberName, lockerName);
    }

    @Override
    @Transactional
    public void sendLockerRejected(Long memberId, String memberName, String lockerName, String reason) {
        // 실제 SSE 발송
        alertScheduledTask.sendLockerRejected(memberId, memberName, lockerName, reason);
    }
}
