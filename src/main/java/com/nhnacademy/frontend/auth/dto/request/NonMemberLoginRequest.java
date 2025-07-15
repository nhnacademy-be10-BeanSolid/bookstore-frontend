package com.nhnacademy.frontend.auth.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NonMemberLoginRequest {
    private String orderNumber;
    private String password;
}
