package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.service.AdminService;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/pointtype")
@RequiredArgsConstructor
public class AdminPointTypeController {

    private static final String REDIRECT_ADMIN_POINT_TYPE = "redirect:/admin/pointtype";

    private final AdminService adminService;

    @GetMapping()
    public String pointTypeForm(Pageable pageable, Model model){

        Page<ResponsePointType> pointTypes = adminService.getAllPointTypes(pageable);

        model.addAttribute("pointTypes", pointTypes.getContent());
        model.addAttribute("page", pointTypes);

        return "admin/pointtype/pointTypeForm";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<Void> registerPointType(@Valid @RequestBody PointTypeCreateRequestDto pointTypeCreateRequestDto, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new ValidationFailedException(bindingResult);
        }

        adminService.addPointType(pointTypeCreateRequestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/register")
    public String registerPointType(){

        return "admin/pointtype/pointType-register";
    }

    @DeleteMapping("/{typeId}")
    @ResponseBody
    public ResponseEntity<Void> deletePointType(@PathVariable Long typeId){

        adminService.deletePointType(typeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{typeId}/isactive")
    @ResponseBody
    public ResponseEntity<Void> changeActive(@PathVariable Long typeId){

        adminService.changeActive(typeId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{typeId}")
    @ResponseBody
    public ResponseEntity<Void> editPointType(@Valid @RequestBody PointTypeUpdateRequestDto requestDto, @PathVariable Long typeId, BindingResult bindingResult){

        if(bindingResult.hasErrors()){
            throw new ValidationFailedException(bindingResult);
        }
        adminService.updatePointType(typeId, requestDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{typeId}/edit")
    public String editPointType(@PathVariable Long typeId, Model model) {

        ResponsePointType responsePointType = adminService.getPointType(typeId);

        model.addAttribute("responsePointType", responsePointType);

        return "admin/pointtype/pointType-edit";
    }
}
