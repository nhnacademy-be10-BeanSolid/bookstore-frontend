package com.nhnacademy.frontend.common.service.impl;


import com.nhnacademy.frontend.common.service.MinioService;
import io.minio.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MinioServiceImpl implements MinioService {
    private final MinioClient minioClient;
    @Value("${spring.minio.review.image.bucket.name}")
    String reviewImageBucketName;

    @Override
    public void createBucket(){
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(reviewImageBucketName).build());

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(reviewImageBucketName).build());
                log.debug("버킷 생성됨: " + reviewImageBucketName);
            } else {
                log.debug("이미 존재하는 버킷: " + reviewImageBucketName);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("버킷 생성 실패", e);
        }
    }

    @Override
    public String uploadImage(MultipartFile file) {
        String objectName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try (InputStream is = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(reviewImageBucketName)
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .bucket(reviewImageBucketName)
                            .object(objectName)
                            .method(Method.GET)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("MinIO 파일 업로드 실패", e);
        }
    }

    @Override
    public void deleteImage(String bucketName, String objectName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()
            );
        } catch (Exception e) {
            log.error("MinIO 이미지 삭제 실패: {}", objectName, e);
        }
    }

}
