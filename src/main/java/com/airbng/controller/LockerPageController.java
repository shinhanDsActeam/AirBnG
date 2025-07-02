package com.airbng.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/page/locker")
public class LockerPageController {

    @GetMapping("/search")
    public String showMapPage() {
        // /WEB-INF/views/map.jsp 파일을 보여줍니다
        return "search";
    }
}
