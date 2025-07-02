package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page/lockerSearchDetails")
public class LockerPageController {

    @GetMapping("/{address}")
    public String showMapPage(@PathVariable String address) {
        System.out.println("Address: " + address);
        return "search";
    }
}
