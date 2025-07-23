package com.nhnacademy.frontend.swagger.controller;

import com.nhnacademy.frontend.swagger.adapter.ApiDocsAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiDocsController {

    private final ApiDocsAdapter apiDocsAdapter;

    @GetMapping(value = "/api-docs/{serviceName}/v3/api-docs", produces = "application/json")
    public ResponseEntity<String> getApiDocs(@PathVariable String serviceName) {
        String apiDocsJson = apiDocsAdapter.getApiDocs(serviceName);
        return ResponseEntity.ok(apiDocsJson);
    }
}