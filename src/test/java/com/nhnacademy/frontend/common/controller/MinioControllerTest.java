package com.nhnacademy.frontend.common.controller;


import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.common.service.MinioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MinioController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = {JwtAuthenticationFilter.class}
                ))
@AutoConfigureMockMvc(addFilters = false)
class MinioControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private MinioService minioService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("버킷 생성 요청 테스트")
    void createBucketTest() throws Exception {
        mockMvc.perform(post("/minio/bucket"))
                .andExpect(status().isOk())
                .andExpect(content().string("버킷 생성 완료 또는 이미 존재함"));

        verify(minioService, times(1)).createBucket();
    }
}
