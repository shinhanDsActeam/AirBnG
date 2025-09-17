package com.airbng.admin.controller;

import com.airbng.admin.dto.response.LockerReviewDetailResponse;
import com.airbng.admin.service.LockerReviewService;
import com.airbng.platform.common.response.BaseResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/lockers")

public class LockerReviewController {

    private final LockerReviewService lockerReviewService;

    //상세보기
    @GetMapping("/{lockerReviewId}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<LockerReviewDetailResponse> findLockerReviewById(@PathVariable Long lockerReviewId) {
        return new BaseResponse<>(lockerReviewService.findLockerReviewById(lockerReviewId));
    }


}
