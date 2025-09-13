package com.airbng.consumer.controller;

import com.airbng.platform.common.response.BaseResponse;
import com.airbng.platform.common.response.status.BaseResponseStatus;
import com.airbng.consumer.service.ZzimService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ZzimController {

    private final ZzimService zzimService;

    /**
     * 찜 등록 또는 취소 (토글 방식)
     */
    @PostMapping("/lockers/{lockerId}/members/{memberId}/zzim")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<BaseResponse<BaseResponseStatus>> toggleZzim(
            @PathVariable Long lockerId,
            @PathVariable Long memberId) {
        BaseResponseStatus status = zzimService.toggleZzim(memberId, lockerId);
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
    }

    /**
     * 찜 여부 확인
     */
    @GetMapping("/lockers/{lockerId}/members/{memberId}/zzim/exists")
    @PreAuthorize("hasAnyAuthority('USER')")
    public ResponseEntity<BaseResponse<Boolean>> existsZzim(@PathVariable Long memberId,
                                                            @PathVariable Long lockerId) {
        boolean exists = zzimService.isExistZzim(memberId, lockerId);
        return ResponseEntity.ok(new BaseResponse<>(exists));
    }
}
