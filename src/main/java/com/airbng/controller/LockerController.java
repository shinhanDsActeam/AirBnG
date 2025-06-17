package com.airbng.controller;

import com.airbng.dto.LockerDTO;
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
    public ResponseEntity<String> registerLocker(@RequestBody LockerDTO dto) {
        lockerService.registerLocker(dto);
        return ResponseEntity
                .ok()
                .contentType(MediaType.valueOf("text/plain;charset=UTF-8"))
                .body("보관소 등록 완료");
    }
}

