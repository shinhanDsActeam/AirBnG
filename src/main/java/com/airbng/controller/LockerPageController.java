package com.airbng.controller;

import com.airbng.dto.locker.LockerSearchRequest;
import com.airbng.dto.locker.LockerSearchResponse;
import com.airbng.service.LockerService;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;


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

    @GetMapping("/lockerSearch")
    public String showFilterMapPage(@RequestParam Long jimTypeId,
                                    Model model) {

        List<Long> jimTypeIdList = (jimTypeId == null) ? null : Collections.singletonList(jimTypeId);

        LockerSearchRequest request = LockerSearchRequest.builder()
                .jimTypeId(jimTypeIdList)
                .build();

        // 서비스 호출
        LockerSearchResponse response = lockerService.findAllLockerBySearch(request);

        System.out.println("Locker Type: " + jimTypeId);

        // JSP에 전달
        //model.addAttribute("address", address);
        model.addAttribute("lockerType", jimTypeId);

        return "searchFilter";
    }
}
