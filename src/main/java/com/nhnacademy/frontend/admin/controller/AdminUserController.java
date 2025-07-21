package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.service.AdminService;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final AdminService adminService;

    @GetMapping()
    public String userForm(@PageableDefault(size=5) Pageable pageable, Model model) {

        Page<ResponseUser> users = adminService.getAllUsers(pageable);

        model.addAttribute("users", users.getContent());
        model.addAttribute("page", users);

        return "admin/user/userForm";
    }

    @GetMapping("/bulk/status")
    public String bulkUpdateUserStatus() {

        adminService.bulkUpdate();

        return "redirect:/admin/users";
    }

}
