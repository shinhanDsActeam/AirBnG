package com.airbng.consumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page")
public class HomePageController {

    @GetMapping("/home")
    public String home() {

        return "home";
    }

}
