package com.nhnacademy.frontend.mypage.controller;

import com.nhnacademy.frontend.mypage.service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;

    @GetMapping
    public String mypageForm() {
        return "mypage/form";
    }

    @PostMapping("/withdraw")
    public String mypageWithdraw(@RequestParam("password") String password) {
        boolean result = mypageService.withdrawUser(password);
        if(result) {
            return "redirect:/";
        } else {
            return "redirect:/mypage";
        }
    }
}
