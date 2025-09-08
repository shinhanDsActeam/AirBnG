package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.jimType.JimTypeResponse;
import com.airbng.service.JimTypeService;
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
