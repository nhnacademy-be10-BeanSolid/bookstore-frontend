package com.nhnacademy.frontend.mypage.domain.request;

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
