package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.MemberLoginResponse;
import com.airbng.service.ReservationAlarmSseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import javax.servlet.http.HttpSession;

@RequestMapping("/alarms")
@RestController
@RequiredArgsConstructor
public class ReservationAlarmSseController {

    private final ReservationAlarmSseService reservationAlarmSseService;

    @GetMapping("/reservations/alarms")
    public SseEmitter subscribe(
            HttpSession session,
            @RequestHeader(value = "Last-Event-ID", required = false) String lastEventId
    ) {
//        테스트 위한 코드
//        Long memberId = 1L; //keeper의 경우 1L로 고정
//
//        Long memberId = 3L; // dropper의 경우 3L로 고정


//        이게 찐 코드
        // 세션에서 로그인한 사용자 정보 가져오기
        MemberLoginResponse userDetails = (MemberLoginResponse) session.getAttribute("memberId");
        if (userDetails == null) {
            throw new IllegalStateException("로그인 정보가 없습니다.");
        }
        Long memberId = userDetails.getMemberId();

        // lastEventId를 로그 또는 서비스로 넘겨서 놓친 알림 재전송할 수 있음
        return reservationAlarmSseService.connect(memberId, lastEventId);
    }
}

