package com.nhnacademy.frontend.swagger.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
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
    private final ObjectMapper objectMapper; // ObjectMapper 주입

    @GetMapping(value = "/api-docs/{serviceName}/v3/api-docs", produces = "application/json")
    public ResponseEntity<String> getApiDocs(@PathVariable String serviceName) {
        String apiDocsJson = apiDocsAdapter.getApiDocs(serviceName);

        try {
            JsonNode rootNode = objectMapper.readTree(apiDocsJson);
            if (rootNode instanceof ObjectNode objectNode) {
                if (objectNode.has("servers")) {
                    objectNode.remove("servers");
                }
            }
            return ResponseEntity.ok(objectMapper.writeValueAsString(rootNode));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error processing API docs: " + e.getMessage());
        }
    }
}