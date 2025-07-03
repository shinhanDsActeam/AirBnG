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
        return "ReservationList";
    }
    @GetMapping("")
    public String reservationDetails(@RequestParam("id") Long reservationId,
                                     HttpSession session,
                                     Model model) {
        Long memberId = (Long) session.getAttribute("memberId");

        model.addAttribute("reservationId", reservationId);
        model.addAttribute("memberId", memberId);
        return "reservationDetails";
    }

    @GetMapping("/form")
    public String getForm(@RequestParam("lockerId") Long lockerId,
                          Model model) {
        System.out.println("Locker ID: " + lockerId);
        model.addAttribute("lockerId", lockerId);
        return "reservationForm";
    }
}
