package com.nhnacademy.frontend.order.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReturnsRequest(

        @NotBlank
        String reason,

        @NotNull
        Boolean damaged
) {}