package com.nhnacademy.frontend.common.adapter.dto.user.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseAddress {
    private long addressId;
    private String addressNickName;
    private String addressDetail;
    private String userId;
}
