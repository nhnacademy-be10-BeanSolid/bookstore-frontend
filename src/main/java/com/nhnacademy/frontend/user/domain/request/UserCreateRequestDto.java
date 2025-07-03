package com.nhnacademy.frontend.user.domain.request;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserCreateRequestDto(
        @NotBlank @Size(max = 20) String userId,
        @NotBlank @Size(max = 255) String userPassword,
        @NotBlank @Size(max = 20) String userName,
        @NotBlank @Size(max = 15) String userPhoneNumber,
        @NotBlank @Email @Size(max = 50) String userEmail,
        @NotNull @Past LocalDate userBirth,
        Boolean isAvailable // 중복체크 결과를 받을 필드 추가 (nullable)
) {}
