package com.nhnacademy.frontend.common.adapter.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePoint {

    private Long pointId;
    private String userId;
    private Long typeId;
    private Long orderNo;
    private LocalDateTime earnedAndUsedAt;
    private String earnedAndUsedPoint;
}