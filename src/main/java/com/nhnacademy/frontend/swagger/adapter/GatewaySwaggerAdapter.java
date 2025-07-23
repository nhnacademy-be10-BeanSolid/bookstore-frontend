package com.nhnacademy.frontend.swagger.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@FeignClient(name = "gateway-service", contextId = "gatewaySwaggerAdapter")
public interface GatewaySwaggerAdapter {

    @GetMapping("/v3/api-docs/swagger-config")
    Map<String, Object> getSwaggerConfig();
}