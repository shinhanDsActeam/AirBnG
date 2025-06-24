package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.domain.base.BaseStatus;
import com.airbng.dto.SampleDTO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Api(tags = "홈 컨트롤러")
@RestController
@RequestMapping("/test")// json 방식으로 응답
@Validated
public class HomeController {

    @ApiOperation("@Valid 테스트 (유효성 검증)")
    @PostMapping
    public BaseResponse home(@RequestBody @Valid SampleDTO sample) {
        return new BaseResponse(BaseResponseStatus.SUCCESS);
    }

    @ApiOperation("파라미터 유효성 검증 테스트")
    @GetMapping
    public BaseResponse getName(@RequestParam(value = "status") @Size(min = 3) String name,
                                @RequestParam(value = "id") @Min(1) @NotNull(message = "빈값이면 안됩니다.") Long id){
        return new BaseResponse(BaseResponseStatus.SUCCESS);
    }

}
