package com.airbng.admin.controller;

import com.airbng.admin.service.LockerReviewService;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;
import com.airbng.platform.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/lockers")
@RequiredArgsConstructor
public class LockerReviewController {

    private final LockerReviewService lockerReviewService;

    @PostMapping("/approve")
    public ResponseEntity<BaseResponse<String>> approve(@RequestParam Long pendingLockerId, @RequestParam Long memberId) {
        boolean result = lockerReviewService.approveLockerReview(pendingLockerId, memberId);
        return ResponseEntity.ok(new BaseResponse<>("보관소 승인 완료"));
    }

    @PostMapping("/reject")
    public ResponseEntity<?> reject(@RequestParam Long pendingLockerId, @RequestParam String reason) {
        LockerReviewRejectCommand result = lockerReviewService.rejectLockerReview(pendingLockerId, reason);
        return ResponseEntity.ok(new BaseResponse<>("보관소 반려"));
    }
}
