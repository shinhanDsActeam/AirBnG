package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.common.response.status.BaseResponseStatus;
import com.airbng.service.ZzimService;
import com.airbng.util.SessionUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<BaseResponse<BaseResponseStatus>> toggleZzim(
            @PathVariable Long lockerId,
            @PathVariable Long memberId,
            HttpSession session) {

        Long sessionMemberId = SessionUtils.getLoginMemberId(session);

        BaseResponseStatus status = zzimService.toggleZzim(sessionMemberId, memberId, lockerId);
        return ResponseEntity.status(status.getHttpStatus())
                .body(new BaseResponse<>(status));
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
