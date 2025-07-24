package com.nhnacademy.frontend.common.adapter.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookTagCreateRequestDto {

    @NotBlank
    @Size(min = 1, max = 50)
    private String tagName;
}