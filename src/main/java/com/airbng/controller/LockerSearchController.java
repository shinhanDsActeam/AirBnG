package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.LockerSearchDTO;
import com.airbng.service.LockerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/swagger/locker")
public class LockerSearchController {

    @Autowired
    private LockerService lockerService;

    @GetMapping("/lockerSearch")
    public ResponseEntity<BaseResponse<LockerSearchDTO>> searchLockers(LockerSearchDTO.Result searchParam) {
        LockerSearchDTO dto = lockerService.lockerSearch(searchParam);
        return ResponseEntity.ok(new BaseResponse<>(dto));
    }

}