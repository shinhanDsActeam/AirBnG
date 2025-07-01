package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page")
public class IndexController {

    @GetMapping("/home")
    public String index() {
        return "home";
    }
}

