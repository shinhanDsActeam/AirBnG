package com.airbng.controller;

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

    @GetMapping("/lockerSearchDetails")
    public String showMapPage(@RequestParam String address,
                              @RequestParam String reservationDate,
                              Model model) {

        LockerSearchRequest request = LockerSearchRequest.builder()
                .address(address)
                .build();

        // 서비스 호출
        LockerSearchResponse response = lockerService.findAllLockerBySearch(request);

        System.out.println("Address: " + address);
        System.out.println("Reservation Date: " + reservationDate);

        // JSP에 전달
        model.addAttribute("address", address);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("lockers", response.getLockers());
        model.addAttribute("count", response.getCount());

        return "search";
    }
}
