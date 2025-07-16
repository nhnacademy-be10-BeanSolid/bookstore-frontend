package com.nhnacademy.frontend.common.controller;

import com.nhnacademy.frontend.common.service.MinioService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("!test")
@RestController
@RequiredArgsConstructor
@RequestMapping("/minio")
public class MinioController {

    private final MinioService minioService;

    @PostMapping("/bucket")
    public ResponseEntity<String> createBucket() {
        minioService.createBucket();
        return ResponseEntity.ok("버킷 생성 완료 또는 이미 존재함");
    }
}