package com.nhnacademy.frontend.common.service.impl;

import io.minio.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @InjectMocks
    private MinioServiceImpl minioService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(minioService, "reviewImageBucketName", bucketName);
    }

    @Test
    @DisplayName("버킷 생성 - 이미 존재하지 않을 때")
    void createBucket_WhenNotExists() throws Exception {
        // given
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        // when
        minioService.createBucket();

        // then
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    @DisplayName("버킷 생성 - 이미 존재할 때")
    void createBucket_WhenExists() throws Exception {
        // given
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        // when
        minioService.createBucket();

        // then
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    @DisplayName("이미지 업로드 - 성공")
    void uploadImage_Success() throws Exception {
        // given
        String originalFilename = "test.jpg";
        MultipartFile file = new MockMultipartFile("image", originalFilename, "image/jpeg", "test-image-content".getBytes());

        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(mock(ObjectWriteResponse.class));

        // when
        String resultUrl = minioService.uploadImage(file);

        // then
        assertNotNull(resultUrl);
        assertTrue(resultUrl.startsWith("/images/review/"));
        assertTrue(resultUrl.endsWith(originalFilename));
        verify(minioClient).putObject(any(PutObjectArgs.class));
        verify(minioClient, never()).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    @DisplayName("이미지 업로드 - 실패 (MinIO 예외)")
    void uploadImage_Failure_MinioException() throws Exception {
        // given
        MultipartFile file = new MockMultipartFile("image", "test.jpg", "image/jpeg", "test-image-content".getBytes());
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException("MinIO error"));

        // when & then
        assertThrows(RuntimeException.class, () -> minioService.uploadImage(file));
    }

    @Test
    @DisplayName("이미지 삭제 - 성공")
    void deleteImage_Success() throws Exception {
        // given
        String objectName = "test-object";

        // when
        minioService.deleteImage(bucketName, objectName);

        // then
        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    @DisplayName("이미지 삭제 - 실패 (MinIO 예외)")
    void deleteImage_Failure_MinioException() throws Exception {
        String objectName = "test-object";
        doThrow(new RuntimeException("MinIO error")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        minioService.deleteImage(bucketName, objectName);

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }
}