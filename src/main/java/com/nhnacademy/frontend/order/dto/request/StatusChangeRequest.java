package com.nhnacademy.frontend.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Setter;

@Setter
public class StatusChangeRequest {

    @NotBlank
    private String newStatus;
    private String memo;
}
