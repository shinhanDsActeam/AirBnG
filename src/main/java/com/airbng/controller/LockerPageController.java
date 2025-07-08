package com.airbng.controller;

import com.airbng.dto.locker.LockerSearchRequest;
import com.airbng.dto.locker.LockerSearchResponse;
import com.airbng.service.LockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/page")
public class LockerPageController {

    @GetMapping("/lockerSearchDetails")
    public String showLockerSearchDetails(@RequestParam String address,
                                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
                                          @RequestParam(required = false) Long jimTypeId,
                                          Model model) {

        log.info("검색 페이지 접근 - address: {}, reservationDate: {}, jimTypeId: {}", address, reservationDate, jimTypeId);

        // JSP에 전달
        model.addAttribute("address", address);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("jimTypeId", jimTypeId);
        model.addAttribute("count", 0);

        return "search";
    }

    @GetMapping("/lockerSearch")
    public String showLockerSearch(@RequestParam(required = false) String address,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reservationDate,
                                   @RequestParam(required = false) Long jimTypeId,
                                   Model model) {
        log.info("필터 검색 페이지 접근 - address: {}, reservationDate: {}, jimTypeId: {}", address, reservationDate, jimTypeId);

        model.addAttribute("address", address);
        model.addAttribute("reservationDate", reservationDate);
        model.addAttribute("jimTypeId", jimTypeId);
        model.addAttribute("count", 0);

        return "searchFilter";
    }
}
