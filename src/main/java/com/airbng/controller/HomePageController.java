package com.airbng.controller;

import com.airbng.security.domain.CustomUserDetails;
import com.airbng.service.ReservationAlarmSseService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page")
public class HomePageController {

    private final ReservationAlarmSseService reservationAlarmSseService;

    @GetMapping("/home")
    public String home() {

        return "home";
    }

}
