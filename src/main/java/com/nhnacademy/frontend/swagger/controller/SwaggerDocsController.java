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
    private static final String HTTP_PROTOCOL = "http://";
    private static final String HTTPS_PROTOCOL = "https://";

    private final GatewaySwaggerAdapter gatewaySwaggerAdapter;

    public SwaggerDocsController(GatewaySwaggerAdapter gatewaySwaggerAdapter) {
        this.gatewaySwaggerAdapter = gatewaySwaggerAdapter;
    }

    @GetMapping("/api-docs-ui")
    public String swaggerDocs(Model model, HttpServletRequest request) {
        try {
            Map<String, Object> swaggerConfig = gatewaySwaggerAdapter.getSwaggerConfig();

            // 현재 요청의 프로토콜 확인
            String currentProtocol = request.getScheme(); // "http" 또는 "https"

            if (swaggerConfig.containsKey(OAUTH2_REDIRECT_URL)) {
                String oauth2RedirectUrl = (String) swaggerConfig.get(OAUTH2_REDIRECT_URL);
                if (oauth2RedirectUrl != null) {
                    // 현재 요청 프로토콜에 맞춰 oauth2RedirectUrl의 프로토콜 변경
                    if (currentProtocol.equals(HTTPS_PROTOCOL.replace("://", "")) && oauth2RedirectUrl.startsWith(HTTP_PROTOCOL)) {
                        swaggerConfig.put(OAUTH2_REDIRECT_URL, oauth2RedirectUrl.replace(HTTP_PROTOCOL, HTTPS_PROTOCOL));
                    } else if (currentProtocol.equals(HTTP_PROTOCOL.replace("://", "")) && oauth2RedirectUrl.startsWith(HTTPS_PROTOCOL)) {
                        // 이 경우는 거의 없겠지만, 혹시 모를 상황 대비
                        swaggerConfig.put(OAUTH2_REDIRECT_URL, oauth2RedirectUrl.replace(HTTPS_PROTOCOL, HTTP_PROTOCOL));
                    }
                }
            }

            // urls 내의 각 API 문서 URL도 HTTPS로 강제 변환 (필요하다면)
            // gateway-service의 application.yml에서 url이 상대경로로 되어 있으므로,
            // 이 부분은 swagger-docs.html의 JS에서 gatewayBaseUrl과 결합될 때 처리될 것입니다.
            // 따라서 여기서는 oauth2RedirectUrl만 수정해도 충분합니다.

            model.addAttribute("swaggerConfig", swaggerConfig);
            return "swagger/swagger-docs";
        } catch (Exception e) {
            log.error("Error fetching Swagger config via Feign Client: {}", e.getMessage(), e);
            model.addAttribute("swaggerConfig", new HashMap<>()); // 빈 맵 전달
            return "swagger/swagger-docs";
        }
    }
}