package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SplashPageController {
    @GetMapping("/")
    public String splash() {
        return "splash";
    }
}
