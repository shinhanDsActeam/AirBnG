package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/page/reservation")
public class ReservaionPageController {
    @GetMapping("/list")
    public String reservationListPage() {

        return "ReservationList"; // Thymeleaf 템플릿 이름
    }
}
