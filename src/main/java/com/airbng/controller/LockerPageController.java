package com.airbng.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Component
@RequestMapping("/page")
public class LockerPageController {

    @GetMapping("/lockers")
    public String lockerPage() {
        return "locker";
    }

    @GetMapping("/lockers/register")
    public String lockerRegisterPage() {
        return "lockerRegister";
    }

    @GetMapping("/lockers/manage")
    public String lockerManagePage() {
        return "lockerManage";
    }

}
