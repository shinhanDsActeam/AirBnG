package com.airbng.admin.controller;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.dto.response.LockerReviewListResponse;
import com.airbng.admin.service.LockerReviewService;
import com.airbng.api.consumer.dto.command.LockerReviewRejectCommand;
import com.airbng.platform.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.airbng.admin.dto.response.LockerReviewDetailResponse;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/admin/lockers")
@RequiredArgsConstructor
public class LockerReviewController {

    private final LockerReviewService lockerReviewService;

    //목록 + 페이징
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @GetMapping
    public Page<LockerReviewListResponse> findAllByReviewStatus(@RequestParam ReviewStatus status, @RequestParam(value = "page", defaultValue = "1") int page) {
        return lockerReviewService.findAllByReviewStatus(status, page);
    }

   //상세보기
    @GetMapping("/{lockerReviewId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<LockerReviewDetailResponse> findLockerReviewById(@PathVariable Long lockerReviewId) {
        return new BaseResponse<>(lockerReviewService.findLockerReviewById(lockerReviewId));
    }

    @PostMapping("/approve")
    public ResponseEntity<BaseResponse<String>> approve(@RequestParam Long pendingLockerId, @RequestParam Long memberId) {
        boolean result = lockerReviewService.approveLockerReview(pendingLockerId, memberId);
        return ResponseEntity.ok(new BaseResponse<>("보관소 승인 완료"));
    }

    @PostMapping("/reject")
    public ResponseEntity<BaseResponse<String>> reject(@RequestParam Long pendingLockerId,  @RequestParam Long memberId, @RequestParam String reason) {
        boolean result = lockerReviewService.rejectLockerReview(pendingLockerId, memberId, reason);
        return ResponseEntity.ok(new BaseResponse<>("보관소 반려"));
    }
}