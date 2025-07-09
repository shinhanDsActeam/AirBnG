package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/page")
public class MemberPageController {
    @GetMapping("/login")
    public String login(@RequestParam(value = "redirect", required = false) String redirect) {
        return "login";
    }

    @GetMapping("/mypage")
    public String mypage() {
        return "mypage";
    }

    @GetMapping("/signup")
    public String signup(@RequestParam(value = "redirect", required = false) String redirect) {
        return "signup";
    }

    @GetMapping("/myInfo")
    public String myInfo() {
        return "myInfo";
    }
}
