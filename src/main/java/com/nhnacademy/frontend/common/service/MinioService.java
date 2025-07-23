package com.nhnacademy.frontend.common.service;

import org.springframework.web.multipart.MultipartFile;

public interface MinioService {
    void createBucket();

    String uploadImage(MultipartFile file);

    void deleteImage(String bucketName, String objectName);
}
