package com.airbng.core.controller;

import com.airbng.platform.common.response.BaseResponse;
import com.airbng.core.dto.jimType.JimTypeResponse;
import com.airbng.core.service.JimTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/jimtypes")
public class JimTypeController {

    private final JimTypeService jimTypeService;

    @GetMapping
    public BaseResponse<List<JimTypeResponse>> getAllJimTypes() {
        return new BaseResponse<>(jimTypeService.findAllJimTypes());
    }

}
