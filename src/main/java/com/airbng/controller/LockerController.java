package com.airbng.controller;

import com.airbng.common.response.BaseResponse;
import com.airbng.dto.UserFindByIdResponse;
import com.airbng.service.LockerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController("/locker")
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/details/{lockerId}")
    public BaseResponse<UserFindByIdResponse> findUserById(@PathVariable Long lockerId) {
        return new BaseResponse<>(lockerService.findUserById(lockerId));
    }
}



//
//@RestController
//public class LockerController {
//    @Autowired
//    private LockerService service;
//
//    @GetMapping("/locker/details.do")
//    public String detailsAll(Model model, LockerDetailsDTO dto) {
//        Map<String, Object> map = service.detailsAll(dto);
//        model.addAttribute("list",map);
//        return "locker/details"; // locker/details.jsp
//    }
//
//}
