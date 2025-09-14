package com.airbng.admin.controller;

import com.airbng.admin.api.PendingLockerInsertRequest;
import com.airbng.admin.domain.base.ReviewStatus;
import com.airbng.admin.domain.review.PendingLocker;
import com.airbng.admin.dto.LockerReviewConfirmResponse;
import com.airbng.admin.dto.LockerReviewDetailResponse;
import com.airbng.admin.dto.LockerReviewListResponse;
import com.airbng.admin.service.LockerReviewService;
import com.airbng.admin.common.response.BaseResponse;
import com.airbng.dto.locker.LockerInsertRequest;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/lockers")
@Validated
public class LockerReviewController {

    private final LockerReviewService lockerReviewService;

    // 등록
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public BaseResponse<PendingLocker> insert(
            @RequestPart("locker") PendingLockerInsertRequest dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        dto.setImage(images); // DTO에 파일들 세팅
        lockerReviewService.insertPendingLocker(dto);
        return new BaseResponse<>(lockerReviewService.insertPendingLocker(dto));
    }


    //상세보기
    @GetMapping("/{lockerReviewId}")
    public BaseResponse<LockerReviewDetailResponse> findLockerReviewById(@PathVariable Long lockerReviewId) {
        return new BaseResponse<>(lockerReviewService.findLockerReviewById(lockerReviewId));
    }

    //목록 + 페이징
    @GetMapping
    public Page<LockerReviewListResponse> findAllByStatus(@RequestParam ReviewStatus status, @RequestParam(value = "page", defaultValue = "0") int page) {
        return lockerReviewService.findAllByStatus(status, page);
    }


    //    //보관소 승인 거절
//    @PatchMapping("/{lockerReview-id}/members/{member-id}/confirm")
//    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    public BaseResponse<LockerReviewConfirmResponse> confirmResponse(
//            @PathVariable("lockerReview-id") @NotNull @Min(1) Long lockerReviewId,
//            @PathVariable("member-id") Long memberId,
//            @RequestParam("approve") String approve) {
//        return new BaseResponse<>(lockerReviewService.confirmLockerReviewState(lockerReviewId, approve, memberId));
//    }


//    @PostMapping("/{id}/approve")
//    public void approve(@PathVariable Long lockerReviewId){
//        lockerReviewService.approve(lockerReviewId);
//    }
//
//    @PostMapping("/{id}/reject")
//    public void reject(@PathVariable Long lockerReviewId, @RequestBody RejectRequest request){
//        lockerReviewService.reject(lockerReviewId, request.getReason());
//    }


}
