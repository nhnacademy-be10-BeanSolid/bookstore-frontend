package com.nhnacademy.frontend.swagger.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List; // List만 필요

@Slf4j
@Controller
public class SwaggerDocsController {

    @Value("${swagger.services}")
    private List<String> swaggerServices;

    @GetMapping("/api-docs-ui")
    public String swaggerDocs(Model model) {
        // 서비스 이름 목록만 Model에 담아서 HTML로 전달
        model.addAttribute("swaggerServices", swaggerServices);
        return "swagger/swagger-docs";
    }
}