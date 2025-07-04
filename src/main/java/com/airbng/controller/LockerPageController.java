package com.airbng.controller;

import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerSearchRequest;
import com.airbng.dto.locker.LockerSearchResponse;
import com.airbng.service.LockerService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequestMapping("/page")
public class LockerPageController {

    private final LockerService lockerService;

    public LockerPageController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @GetMapping ("/lockerDetails")
    public String showMapPage(@RequestParam Long lockerId,
                              Model model) {

        LockerDetailResponse request = LockerDetailResponse.builder()
                .lockerId(lockerId)
                .build();

//        LockerDetailResponse response = lockerService.findUserById(lockerId);

        // 서비스 호출
        LockerDetailResponse response = lockerService.findUserById(request.getLockerId());

        System.out.println("lockerId: " + lockerId);

        // JSP에 전달
        model.addAttribute("lockerId", lockerId);
        model.addAttribute("lockerDetail", response);

        return "lockerDetails"; // lockerDetails.jsp로 이동
    }

    @GetMapping("/lockerSearchDetails")
    public String showMapPage(@RequestParam String address,
                              @RequestParam String reservationDate,
                              Model model) {

        model.addAttribute("address", address);
        model.addAttribute("reservationDate", reservationDate);

        return "search";
    }
}
