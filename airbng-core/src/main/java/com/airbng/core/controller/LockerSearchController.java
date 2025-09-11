package com.airbng.core.controller;

import com.airbng.platform.common.response.BaseResponse;
import com.airbng.core.dto.locker.LockerSearchRequest;
import com.airbng.core.dto.locker.LockerSearchResponse;
import com.airbng.core.service.LockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/lockers")
public class LockerSearchController {

    private final LockerService lockerService;

    @GetMapping
    public BaseResponse<LockerSearchResponse> findAllLockerBySearch(
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String lockerName,
            @RequestParam(required = false) Long jimTypeId
    ) {
        List<Long> jimTypeIdList = (jimTypeId == null) ? null : Collections.singletonList(jimTypeId);

        LockerSearchRequest request = LockerSearchRequest.builder()
                .address(address)
                .lockerName(lockerName)
                .jimTypeId(jimTypeIdList)
                .build();

        LockerSearchResponse result = lockerService.findAllLockerBySearch(request);

        return new BaseResponse<>(result);
    }

}