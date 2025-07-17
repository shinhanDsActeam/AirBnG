package com.airbng.controller;

import com.airbng.dto.locker.LockerDetailResponse;
import com.airbng.dto.locker.LockerUpdateResponse;
import com.airbng.service.LockerService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
@Slf4j
@Controller
@RequestMapping("/page")
public class LockerPageController {

    private final LockerService lockerService;

    public LockerPageController(LockerService lockerService) {
        this.lockerService = lockerService;
    }

    @GetMapping("/lockers")
    public String lockerPage(HttpSession session, Model model) {
        Long memberId = (Long) session.getAttribute("memberId");
        boolean isLoggedIn = memberId != null;
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            boolean isExistLocker = lockerService.isExistLocker(memberId);
            model.addAttribute("isExistLocker", isExistLocker);

            if (isExistLocker) {
                LockerDetailResponse lockerDetail = lockerService.findMyLocker(memberId);
                model.addAttribute("lockerDetail", lockerDetail);
            }
        }

        return "locker";
    }

    @GetMapping("/lockers/register")
    public String lockerRegisterPage() {
        return "lockerRegister";
    }

    @GetMapping("/lockers/manage")
    public String lockerManagePage(@RequestParam Long lockerId, Model model) {
        LockerUpdateResponse lockerDetail = lockerService.findUpdateUserById(lockerId);
        model.addAttribute("lockerDetail", lockerDetail);
        return "lockerManage";
    }

    @GetMapping ("/lockerDetails")
    public String showMapPage(@RequestParam Long lockerId,
                              Model model) {

        LockerDetailResponse request = LockerDetailResponse.builder()
                .lockerId(lockerId)
                .build();

        // 서비스 호출
        LockerDetailResponse response = lockerService.findUserById(request.getLockerId());

        // JSP에 전달
        model.addAttribute("lockerId", lockerId);
        model.addAttribute("lockerDetail", response);

        return "lockerDetails"; // lockerDetails.jsp로 이동
    }

    @GetMapping("/lockerSearchDetails")
    public String showMapPage(@RequestParam String address,
                              @RequestParam(required = false) Long jimTypeId,
                              @RequestParam(required = false) String reservationDate,
                              Model model) {

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