package com.airbng.controller;

import com.airbng.service.MemberService;
import com.airbng.service.ReservationAlarmSseService;
import com.airbng.service.ReservationAlarmSseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
@RequestMapping("/page")
public class AlarmPageController {

    private final ReservationAlarmSseService reservationAlarmSseService;

    @GetMapping("/notification")
    public String notificationPage(HttpSession session) {

        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId != null) {
            reservationAlarmSseService.markAllAsRead(memberId);   //알림 읽음 처리
            session.setAttribute("hasUnreadAlarm", false);  // dot 제거
        }

        return "notification";
    }

}
