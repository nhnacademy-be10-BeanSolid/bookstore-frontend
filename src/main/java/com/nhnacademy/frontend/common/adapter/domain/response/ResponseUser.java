package com.nhnacademy.frontend.common.adapter.domain.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class ResponseUser {
    private Long userNo;
    private String userId;
    private String userPassword;
    private String userName;
    private String userPhoneNumber;
    private String userEmail;
    private LocalDate userBirth;
    private int userPoint;
    private boolean isAuth;
    private String userStatus;
    private LocalDateTime lastLoginAt;
    private String userGradeName;
}
