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

    private final AdminService adminService;

    @GetMapping("/main")
    public String mainPage() {

        return "admin/main";
    }

    @GetMapping("/pointtype")
    public String PointTypeForm(Pageable pageable, Model model){

        Page<ResponsePointType> pointTypes = adminService.getAllPointTypes(pageable);

        model.addAttribute("pointTypes", pointTypes.getContent());
        model.addAttribute("page", pointTypes);

        return "admin/pointTypeForm";
    }

    @PostMapping("/pointtype/register")
    public String registerPointType(@Valid @ModelAttribute PointTypeCreateRequestDto pointTypeCreateRequestDto, BindingResult bindingResult, RedirectAttributes redirectAttributes){

        if(bindingResult.hasErrors()){
            throw new ValidationFailedException(bindingResult);
        }

        try {
            adminService.addPointType(pointTypeCreateRequestDto);
            redirectAttributes.addFlashAttribute("registerSuccess", "등록 성공!");
        } catch (FeignException e) {
            redirectAttributes.addFlashAttribute("registerFail", "등록 실패!");
        }

        return "redirect:/admin/pointtype";
    }

    @GetMapping("/pointtype/register")
    public String registerPointType(){

        return "admin/pointType-register";
    }

    @DeleteMapping("/pointtype/{typeId}")
    public String deletePointType(@PathVariable Long typeId, RedirectAttributes redirectAttributes){

        try{
            adminService.deletePointType(typeId);
            redirectAttributes.addFlashAttribute("deleteSuccess", "삭제 성공!");
        } catch (FeignException e){
            redirectAttributes.addFlashAttribute("deleteFail", "삭제 실패! 포인트 db 확인 필요!");
        }

        return "redirect:/admin/pointtype";
    }

    @PutMapping("/pointtype/{typeId}/isactive")
    public String changeActive(@PathVariable Long typeId, RedirectAttributes redirectAttributes){

        adminService.changeActive(typeId);

        redirectAttributes.addFlashAttribute("changeSuccess", "변경 성공!");

        return "redirect:/admin/pointtype";
    }

    @PutMapping("/pointtype/{typeId}/edit")
    public String editPointType(@Valid @ModelAttribute PointTypeUpdateRequestDto requestDto, @PathVariable Long typeId, RedirectAttributes redirectAttributes){

        adminService.updatePointType(typeId, requestDto);

        redirectAttributes.addFlashAttribute("editSuccess", "수정 성공!");

        return "redirect:/admin/pointtype";
    }

    @GetMapping("/pointtype/{typeId}/edit")
    public String editPointType(@PathVariable Long typeId, Model model){

        ResponsePointType responsePointType = adminService.getPointType(typeId);

        model.addAttribute("responsePointType", responsePointType);

        return "admin/pointType-edit";
        @GetMapping
    public String admin() {
        return "admin/admin-home";
    }
}
