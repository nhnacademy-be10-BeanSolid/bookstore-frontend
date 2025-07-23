package com.nhnacademy.frontend.swagger.controller;

import com.nhnacademy.frontend.swagger.adapter.GatewaySwaggerAdapter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Controller
public class SwaggerDocsController {

    private static final String OAUTH2_REDIRECT_URL = "oauth2RedirectUrl";

    private final GatewaySwaggerAdapter gatewaySwaggerAdapter;

    public SwaggerDocsController(GatewaySwaggerAdapter gatewaySwaggerAdapter) {
        this.gatewaySwaggerAdapter = gatewaySwaggerAdapter;
    }

    @GetMapping("/api-docs-ui")
    public String swaggerDocs(Model model, HttpServletRequest request) {
        try {
            Map<String, Object> swaggerConfig = gatewaySwaggerAdapter.getSwaggerConfig();

            // oauth2RedirectUrl을 https://bookstore-beansolid.store/webjars/swagger-ui/oauth2-redirect.html 로 강제 재정의
            // 브라우저가 직접 접근 가능한 프론트엔드 도메인을 사용하도록 함
            swaggerConfig.put(OAUTH2_REDIRECT_URL, "https://bookstore-beansolid.store/webjars/swagger-ui/oauth2-redirect.html");

            model.addAttribute("swaggerConfig", swaggerConfig);
            return "swagger/swagger-docs";
        } catch (Exception e) {
            log.error("Error fetching Swagger config via Feign Client: {}", e.getMessage(), e);
            model.addAttribute("swaggerConfig", new HashMap<>()); // 빈 맵 전달
            return "swagger/swagger-docs";
        }
    }
}