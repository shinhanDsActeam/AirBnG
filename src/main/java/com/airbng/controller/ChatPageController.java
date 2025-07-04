package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class ChatPageController {

    @GetMapping("/chatList")
    public String pageList() {
        return "chatList";
    }
}
