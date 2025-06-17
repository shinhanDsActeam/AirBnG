package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.LockerInsertRequest;
import com.airbng.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/locker")
public class LockerController {

    private final LockerService lockerService;

    @PostMapping("/register")
    public BaseResponse<String> registerLocker(@RequestBody LockerInsertRequest dto) {
        lockerService.registerLocker(dto);
        return new BaseResponse<>("보관소 등록 완료");
//        return ResponseEntity
//                .ok()
//                .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
//                .body("보관소 등록 완료");
    }
}

