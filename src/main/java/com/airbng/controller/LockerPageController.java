package com.airbng.controller;

import com.airbng.dto.locker.LockerSearchRequest;
import com.airbng.dto.locker.LockerSearchResponse;
import com.airbng.service.LockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/page")
public class LockerPageController {

    private final LockerService lockerService;

    public LockerPageController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @GetMapping("/lockerSearchDetails")
    public String showLockerSearchDetails(@RequestParam String address,
                                          @RequestParam String reservationDate,
                                          @RequestParam(required = false) Long jimTypeId,
                                          Model model) {

        LockerSearchRequest request = LockerSearchRequest.builder()
                .address(address)
                .jimTypeId((jimTypeId != null) ? Collections.singletonList(jimTypeId) : null)
                .build();

        // 서비스 호출
        LockerSearchResponse response = lockerService.findAllLockerBySearch(request);

        System.out.println("Address: " + address);
        System.out.println("Reservation Date: " + reservationDate);

        // JSP에 전달
        model.addAttribute("address", address);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("jimTypeId", jimTypeId);
        model.addAttribute("lockers", response.getLockers());
        model.addAttribute("count", response.getCount());

        return "search";
    }

    @GetMapping("/lockerSearch")
    public String showLockerSearch(@RequestParam(required = false) String address,
                                   @RequestParam(required = false) String reservationDate,
                                   @RequestParam(required = false) Long jimTypeId,
                                   Model model) {
        log.info("받은 jimTypeId: {}", jimTypeId);

        LockerSearchRequest request = LockerSearchRequest.builder()
                .address(address)
                .jimTypeId((jimTypeId != null) ? Collections.singletonList(jimTypeId) : null)
                .build();

        LockerSearchResponse response = lockerService.findAllLockerBySearch(request);

        model.addAttribute("address", address);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("lockers", response.getLockers());
        model.addAttribute("count", response.getCount());
        model.addAttribute("jimTypeId", jimTypeId);

        return "searchFilter";
    }
}
