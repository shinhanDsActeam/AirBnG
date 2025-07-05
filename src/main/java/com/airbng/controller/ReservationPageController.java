package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/page/reservations")
public class ReservationPageController {

    @GetMapping("/list")
    public String reservationListPage() {
        return "reservation/reservationList";
    }

    @GetMapping("")
    public String reservationDetail(@RequestParam("id") Long reservationId,
                                    HttpSession session,
                                    Model model) {
        Long memberId = (Long) session.getAttribute("memberId");

        model.addAttribute("reservationId", reservationId);
        model.addAttribute("memberId", memberId);
        return "reservation/reservationDetail";
    }

    @GetMapping("/form")
    public String getForm(@RequestParam(value = "lockerId", defaultValue = "0") Long lockerId,
                          Model model) {
        model.addAttribute("lockerId", lockerId);
        return "reservation/reservationForm";
    }
}
