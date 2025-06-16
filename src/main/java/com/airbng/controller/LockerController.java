package com.airbng.controller;

import com.airbng.dto.LockerDTO;
import com.airbng.service.LockerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/locker")
@RequiredArgsConstructor
public class LockerController {

    private final LockerService lockerService;

    @PostMapping("/register")
    public String registerLocker(@RequestBody LockerDTO dto) {
        lockerService.registerLocker(dto);
        return "보관소 등록 완료!";
    }
}
