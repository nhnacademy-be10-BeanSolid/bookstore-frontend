package com.nhnacademy.frontend.user.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserIdCheckRequestDto {
    @NotBlank
    @Size(max = 20)
    private String userId;
}
