package com.airbng.controller;

import com.airbng.dto.locker.LockerPreviewResult;
import com.airbng.domain.base.ReservationState;
import com.airbng.mappers.LockerMapper;
import com.airbng.service.ReservationAlarmSseService;
import com.airbng.service.ReservationAlarmSseServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page")
public class HomePageController {

    private final ReservationAlarmSseService reservationAlarmSseService;

    @GetMapping("/home")
    public String home(HttpSession session) {
        Long memberId = (Long) session.getAttribute("memberId");

        if (memberId != null) {
            boolean hasUnread = reservationAlarmSseService.hasUnreadAlarm(memberId);
            session.setAttribute("hasUnreadAlarm", hasUnread);
        }

        return "home";
    }

}
