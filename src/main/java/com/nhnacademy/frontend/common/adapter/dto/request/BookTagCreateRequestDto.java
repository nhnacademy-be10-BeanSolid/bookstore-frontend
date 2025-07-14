package com.nhnacademy.frontend.common.adapter.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookTagCreateRequestDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String tagName;
}