package com.nhnacademy.frontend.common.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Controller
public class ImageProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${minio.url}")
    private String minioEndpoint;

    @Value("${spring.minio.review.image.bucket.name}")
    private String reviewImageBucketName;

    @GetMapping("/images/review/{objectName}")
    public ResponseEntity<byte[]> getReviewImage(@PathVariable String objectName) {
        URI uri = UriComponentsBuilder
                .fromHttpUrl(minioEndpoint)
                .pathSegment(reviewImageBucketName, objectName)
                .build(true)
                .toUri();

        return restTemplate.exchange(uri, HttpMethod.GET, null, byte[].class);
    }
}
