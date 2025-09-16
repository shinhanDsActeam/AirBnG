package com.airbng.admin.controller;

import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.dto.response.LockerReviewListResponse;
import com.airbng.admin.service.LockerReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
