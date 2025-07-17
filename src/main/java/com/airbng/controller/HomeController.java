package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.dto.SampleDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//@Api(tags = "홈 컨트롤러")
@RestController
@RequestMapping("/test")// json 방식으로 응답
@Validated
public class HomeController {

//    @ApiOperation("@Valid 테스트 (유효성 검증)")
    @PostMapping
    public BaseResponse home(@RequestBody @Valid SampleDTO sample) {
        return new BaseResponse(BaseResponseStatus.SUCCESS);
    }

//    @ApiOperation("파라미터 유효성 검증 테스트")
    @GetMapping("/{id}")
    public BaseResponse getName(@PathVariable(value = "id") @Min(1) @NotNull Long id,
                                @RequestParam(value = "name") @Size(min = 3) String name,
                                @RequestParam(value = "age") @Min(10) int age){
        return new BaseResponse(BaseResponseStatus.SUCCESS);
    }

}
