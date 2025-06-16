package com.airbng.controller;


import com.airbng.domain.Member;
import com.airbng.dto.MemberSignupRequestDTO;
import com.airbng.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;


    @PostMapping("/signup")
    public String signup(@ModelAttribute MemberSignupRequestDTO requestDto) {
        memberService.signup(requestDto);
        return "redirect:/";
    }
}
