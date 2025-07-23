package com.nhnacademy.frontend.swagger.controller;

import com.nhnacademy.frontend.swagger.adapter.GatewaySwaggerAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class SwaggerDocsController {

    private final GatewaySwaggerAdapter gatewaySwaggerAdapter; // Feign Client 주입

    public SwaggerDocsController(GatewaySwaggerAdapter gatewaySwaggerAdapter) {
        this.gatewaySwaggerAdapter = gatewaySwaggerAdapter;
    }

    @GetMapping("/api-docs-ui")
    public String swaggerDocs(Model model) {
        try {
            // Feign Client를 사용하여 게이트웨이의 Swagger 설정 가져오기
            Map<String, Object> swaggerConfig = gatewaySwaggerAdapter.getSwaggerConfig();

            // 가져온 swaggerConfig 맵 자체를 모델에 추가하여 Thymeleaf 템플릿으로 전달합니다.
            model.addAttribute("swaggerConfig", swaggerConfig);
            return "swagger/swagger-docs";
        } catch (Exception e) {
            System.err.println("Error fetching Swagger config via Feign Client: " + e.getMessage());
            // 오류 발생 시 빈 모델을 전달하거나 오류 메시지를 포함할 수 있습니다.
            // swagger-docs.html에서 이 경우를 처리하도록 되어 있습니다.
            model.addAttribute("swaggerConfig", new java.util.HashMap<>()); // 빈 맵 전달
            return "swagger/swagger-docs";
        }
    }
}