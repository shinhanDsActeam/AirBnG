package com.airbng.controller;

import com.airbng.dto.locker.LockerPreviewResult;
import com.airbng.domain.base.ReservationState;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/page")
public class HomePageController {

    private final LockerMapper lockerMapper;

    @GetMapping("/home")
    public String home() {
        return "home";
    }

}
