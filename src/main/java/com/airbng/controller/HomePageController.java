package com.airbng.controller;

import com.airbng.dto.locker.LockerPreviewResult;
import com.airbng.domain.base.ReservationState;
import com.airbng.mappers.LockerMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomePageController {

    private final LockerMapper lockerMapper;

    @GetMapping("/page/home")
    public String home() {
        return "home"; // ✨ 더 이상 popularLockers를 모델에 넣지 않음
    }

}
