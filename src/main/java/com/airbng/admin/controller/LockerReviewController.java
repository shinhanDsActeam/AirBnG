package com.airbng.admin.controller;

import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.service.LockerReviewService;

import com.airbng.admin.common.response.BaseResponse;
import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerTop5Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/lockers")
@Validated
public class LockerReviewController {

    private final LockerReviewService lockerReviewService;

    //상세보기
    @GetMapping("/{lockerId}")
    public BaseResponse<LockerReviewDetailResponse> findLockerById(@PathVariable Long lockerReviewId) {
        return new BaseResponse<>(lockerReviewService.findLockerReviewById(lockerReviewId));
    }


//    @GetMapping("/popular")
//    public BaseResponse<LockerTop5Response> selectTop5Lockers() {
//        log.info("LockerController.selectTop5Lockers");
//        return new BaseResponse<>(lockerReviewService.findTop5Locker());
//    }
}
