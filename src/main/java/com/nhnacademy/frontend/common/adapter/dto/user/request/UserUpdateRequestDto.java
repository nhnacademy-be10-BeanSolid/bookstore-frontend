package com.nhnacademy.frontend.common.adapter.dto.user.request;

import jakarta.validation.constraints.Email;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

public record UserUpdateRequestDto (
        String userPassword,
        String userName,
        String userPhoneNumber,
        @Email String userEmail,
        @CreatedDate LocalDate userBirth){
}
