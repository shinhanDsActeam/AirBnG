package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AlarmPageTestController {

    @GetMapping("/alarms/sse-test")
    public String redirectToStaticHtml() {
        return "redirect:/alarm.html";
    }
}