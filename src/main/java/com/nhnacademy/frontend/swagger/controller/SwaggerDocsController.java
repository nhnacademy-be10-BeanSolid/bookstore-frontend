package com.nhnacademy.frontend.swagger.controller;

import com.nhnacademy.frontend.swagger.adapter.GatewaySwaggerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class SwaggerDocsController {


    private final GatewaySwaggerAdapter gatewaySwaggerAdapter;

    public SwaggerDocsController(GatewaySwaggerAdapter gatewaySwaggerAdapter) {
        this.gatewaySwaggerAdapter = gatewaySwaggerAdapter;
    }

    @GetMapping("/api-docs-ui")
    public String swaggerDocs(Model model) {
        try {
            Map<String, Object> swaggerConfig = gatewaySwaggerAdapter.getSwaggerConfig();
            model.addAttribute("swaggerConfig", swaggerConfig);
            return "swagger/swagger-docs";
        } catch (Exception e) {
            log.error("Error fetching Swagger config via Feign Client: {}", e.getMessage(), e);
            model.addAttribute("swaggerConfig", new HashMap<>()); // 빈 맵 전달
            return "swagger/swagger-docs";
        }
    }
}