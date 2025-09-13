package com.airbng.admin.controller;

import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewConfirmResponse;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.service.LockerReviewService;
import com.airbng.admin.common.response.BaseResponse;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/lockers")
@Validated
public class LockerReviewController {

    private final LockerReviewService lockerReviewService;

    //상세보기
    @GetMapping("/{lockerReviewId}")
    public BaseResponse<LockerReviewDetailResponse> findLockerReviewById(@PathVariable Long lockerReviewId) {
        return new BaseResponse<>(lockerReviewService.findLockerReviewById(lockerReviewId));
    }

//    @GetMapping("pendingLockers/{lockerReviewId}")
//    public BaseResponse<LockerReviewDetailResponse> findPendingLockerReviewById(@PathVariable Long lockerReviewId) {
//        return new BaseResponse<>(lockerReviewService.findPendingLockerReviewById(lockerReviewId));
//    }
//
//    @GetMapping("approvedLockers/{lockerReviewId}")
//    public BaseResponse<LockerReviewDetailResponse> findApprovedLockerReviewById(@PathVariable Long lockerReviewId) {
//        return new BaseResponse<>(lockerReviewService.findApprovedLockerReviewById(lockerReviewId));
//    }
//
//    @GetMapping("rejectedLockers/{lockerReviewId}")
//    public BaseResponse<LockerReviewDetailResponse> findRejectedLockerReviewById(@PathVariable Long lockerReviewId) {
//        return new BaseResponse<>(lockerReviewService.findRejectedLockerReviewById(lockerReviewId));
//    }

    //보관소 승인 거절
    @PatchMapping("/{lockerReview-id}/members/{member-id}/confirm")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<LockerReviewConfirmResponse> confirmResponse(
            @PathVariable("lockerReview-id") @NotNull @Min(1) Long lockerReviewId,
            @PathVariable("member-id") Long memberId,
            @RequestParam("approve") String approve) {
        return new BaseResponse<>(lockerReviewService.confirmLockerReviewState(lockerReviewId, approve, memberId));
    }

    //목록 + 페이징
    @GetMapping
    public Page<PendingLocker> list(@RequestParam(value = "page", defaultValue = "0") int page) {
        return lockerReviewService.findAll(page);
    }
}
