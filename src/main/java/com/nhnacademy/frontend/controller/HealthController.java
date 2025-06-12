package com.nhnacademy.frontend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class HealthController {
    @GetMapping("/actuator/health")
    public Map<String, Object> health() {
        return Collections.singletonMap("status", "UP");
    }
}
