package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.locker.*;
import com.airbng.service.LockerService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static com.airbng.common.response.status.BaseResponseStatus.SUCCESS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lockers")
@Validated
public class LockerController {

    private final LockerService lockerService;


    @GetMapping("/{lockerId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<LockerDetailResponse> findLockerById(@PathVariable Long lockerId) {

        return new BaseResponse<>(lockerService.findLockerById(lockerId));
    }

    @PatchMapping("/{lockerId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<BaseResponseStatus> updateLockerActivation(@PathVariable("lockerId") @NotNull @Min(1) Long lockerId) {
        lockerService.updateLockerActivation(lockerId);
        return new BaseResponse<>(SUCCESS);
    }

    @DeleteMapping("/{lockerId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<String> deleteLocker(@PathVariable Long lockerId) {
        lockerService.deleteLocker(lockerId);
        return new BaseResponse<>("보관소 삭제 완료");
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<BaseResponse<String>> registerLocker(
            @RequestPart("locker") LockerInsertRequest dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        dto.setImages(images); // DTO에 파일들 세팅
        lockerService.registerLocker(dto);
        return ResponseEntity.ok(new BaseResponse<>("보관소 등록 완료"));
    }


    @GetMapping("/popular")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<LockerTop5Response> selectTop5Lockers() {
        log.info("LockerController.selectTop5Lockers");
        return new BaseResponse<>(lockerService.findTop5Locker());
    }

    @GetMapping("/update/{lockerId}")
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<LockerUpdateResponse> findLockerForUpdate(@PathVariable Long lockerId) {
        return new BaseResponse<>(lockerService.findUpdateUserById(lockerId));
    }

    @GetMapping("/my/{memberId}")
    public BaseResponse<LockerDetailResponse> findLockerByMemberId(@PathVariable Long memberId){
        return new BaseResponse<>(lockerService.findMyLocker(memberId));
    }


    @PostMapping(value = "/update/{lockerId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('USER')")
    public BaseResponse<String> updateLocker(
            @PathVariable Long lockerId,
            @RequestPart("locker") LockerUpdateRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images
    ) throws IOException {
        request.setLockerId(lockerId);
        request.setImages(images);
        lockerService.updateLocker(request);
        return new BaseResponse<>("보관소 수정 완료");
    }

}
