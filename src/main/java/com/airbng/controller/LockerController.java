package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerInsertRequest;
import com.airbng.dto.locker.LockerTop5Response;
import com.airbng.service.LockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
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
    public BaseResponse<LockerDetailResponse> findLockerById(@PathVariable Long lockerId) {

        return new BaseResponse<>(lockerService.findUserById(lockerId));
    }

    @PatchMapping("/{lockerId}")
    public BaseResponse<BaseResponseStatus> updateLockerActivation(@PathVariable("lockerId") @NotNull @Min(1) Long lockerId) {
        lockerService.updateLockerActivation(lockerId);
        return new BaseResponse<>(SUCCESS);
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BaseResponse<String>> registerLocker(
            @RequestPart("locker") LockerInsertRequest dto,
            @RequestPart(value = "images", required = false) List<MultipartFile> images) throws IOException {

        dto.setImages(images); // DTO에 파일들 세팅
        lockerService.registerLocker(dto);
        return ResponseEntity.ok(new BaseResponse<>("보관소 등록 완료"));
    }


    @GetMapping("/popular")
    public BaseResponse<LockerTop5Response> selectTop5Lockers() {
        log.info("LockerController.selectTop5Lockers");
        return new BaseResponse<>(lockerService.findTop5Locker());
    }

}
