package com.nhnacademy.frontend.common.adapter.dto.user.response;

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

    public String getGradeName() {
        return this.userGradeName;
    }
    public ResponseUser(Long userNo, String userId, String userPassword, String userName,
                        String userPhoneNumber, String userEmail, LocalDate userBirth,
                        int userPoint, boolean isAuth, String userStatus,
                        LocalDateTime lastLoginAt, String userGradeName) {
        this.userNo = userNo;
        this.userId = userId;
        this.userPassword = userPassword;
        this.userName = userName;
        this.userPhoneNumber = userPhoneNumber;
        this.userEmail = userEmail;
        this.userBirth = userBirth;
        this.userPoint = userPoint;
        this.isAuth = isAuth;
        this.userStatus = userStatus;
        this.lastLoginAt = lastLoginAt;
        this.userGradeName = userGradeName;
    }
}
