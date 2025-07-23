package com.nhnacademy.frontend.swagger.adapter;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "gateway-service", contextId = "apiDocsAdapter")
public interface ApiDocsAdapter {

    @GetMapping("/{serviceName}/v3/api-docs")
    String getApiDocs(@PathVariable("serviceName") String serviceName);
}