package com.airbng.controller;

import com.airbng.common.exception.LockerException;
import java.util.Map;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.LockerDetailResponse;
import com.airbng.dto.UserFindByIdResponse;
import com.airbng.dto.LockerTop5Response;
import com.airbng.service.LockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import static com.airbng.common.response.status.BaseResponseStatus.NOT_FOUND_LOCKERDETAILS;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/lockers")
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/{lockerId}")
    public BaseResponse<LockerDetailResponse> findUserById(@PathVariable Long lockerId) {

        return new BaseResponse<>(lockerService.findUserById(lockerId));
    }

    @GetMapping("/popular")
    public BaseResponse<LockerTop5Response> selectTop5Lockers(){
        log.info("LockerController.selectTop5Lockers");
        return new BaseResponse<>(lockerService.findTop5Locker());
    }

}
