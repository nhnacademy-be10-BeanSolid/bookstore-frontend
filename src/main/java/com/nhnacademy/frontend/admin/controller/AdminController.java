package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import com.nhnacademy.frontend.admin.service.AdminService;
import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import feign.FeignException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    @GetMapping
    public String admin() {
        return "admin/admin-home";
    }
}
