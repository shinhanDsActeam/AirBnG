package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.service.ZzimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ZzimController {

    private final ZzimService zzimService;

    /**
     * 찜 등록 또는 취소 (토글 방식)
     */
    @PostMapping("/lockers/{lockerId}/members/{memberId}/zzim")
    public ResponseEntity<BaseResponse<String>> toggleZzim(@PathVariable Long memberId,
                                                           @PathVariable Long lockerId) {
        boolean added = zzimService.toggleZzim(memberId, lockerId);
        String message = added ? "찜 등록 완료" : "찜 취소 완료";
        return ResponseEntity.ok(new BaseResponse<>(message));
    }

    /**
     * 찜 여부 확인
     */
    @GetMapping("/lockers/{lockerId}/members/{memberId}/zzim/exists")
    public ResponseEntity<BaseResponse<Boolean>> existsZzim(@PathVariable Long memberId,
                                                            @PathVariable Long lockerId) {
        boolean exists = zzimService.isExistZzim(memberId, lockerId);
        return ResponseEntity.ok(new BaseResponse<>(exists));
    }
}
