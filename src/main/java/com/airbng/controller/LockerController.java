package com.airbng.controller;

import java.util.Map;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.UserFindByIdResponse;
import com.airbng.dto.LockerTop5Response;
import com.airbng.service.LockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/locker")
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/details/{lockerId}")
    public BaseResponse<UserFindByIdResponse> findUserById(@PathVariable Long lockerId) {
        return new BaseResponse<>(lockerService.findUserById(lockerId));
    }
}

    @GetMapping("/popular")
    public BaseResponse<LockerTop5Response> selectTop5Lockers(){
        log.info("LockerController.selectTop5Lockers");
        return new BaseResponse<>(lockerService.findTop5Locker());
    }

}
