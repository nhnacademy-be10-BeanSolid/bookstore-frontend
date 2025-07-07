package com.nhnacademy.frontend.mypage.controller;

import com.nhnacademy.frontend.common.adapter.domain.response.ResponseUser;
import com.nhnacademy.frontend.mypage.service.MypageService;
import com.nhnacademy.frontend.user.domain.request.UserUpdateRequestDto;
import com.nhnacademy.frontend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MypageController {
    private final MypageService mypageService;
    private final UserService userService;

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

    @GetMapping("/edit")
    public String mypageEditForm(Model model) {
        ResponseUser user = userService.getMyInfo();
        model.addAttribute("user", user);
        return "mypage/edit";
    }

    @PutMapping("/edit")
    @ResponseBody
    public ResponseEntity<Void> mypageEdit(@RequestBody UserUpdateRequestDto userUpdateRequestDto) {
        userService.updatePersonalInformation(userUpdateRequestDto);
        return ResponseEntity.ok().build();
    }
}
