package com.nhnacademy.frontend.user.controller;

import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.user.domain.response.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Objects;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserTestController {

    private final UserAdapter userAdapter;

    // 에러 페이지 테스트용 컨트롤러임, 필요 없을 시 삭제해도 될듯
    @GetMapping("/{userId}")
    public String user(@PathVariable("userId") String userId, Model model) {

        UserResponseDto responseDto = userAdapter.getUser(userId).getBody();

        model.addAttribute("userId", Objects.requireNonNull(responseDto).getUserName());
        return "user-test";
    }
}
