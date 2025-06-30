package com.nhnacademy.frontend.auth.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseDto<T> {
    private boolean success;
    private String message;
    private T data;
}
