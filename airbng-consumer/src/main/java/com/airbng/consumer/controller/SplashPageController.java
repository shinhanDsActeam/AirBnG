package com.airbng.consumer.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SplashPageController {
    @GetMapping("/")
    public String splash() {
        return "splash";
    }
}