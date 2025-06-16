package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.PopularLockerDTO;
import com.airbng.service.LockerService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController("/locker")
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/popular")
    public BaseResponse<PopularLockerDTO> selectTop5Lockers(){
        log.info("LockerController.selectTop5Lockers");
        return new BaseResponse<>(lockerService.selectTop5Locker());
    }

}
