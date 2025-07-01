package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ReservaionPageController {
    @GetMapping("/page/reservationlist")
    public String reservationListPage() {

        return "ReservationList"; // Thymeleaf 템플릿 이름
    }
}
