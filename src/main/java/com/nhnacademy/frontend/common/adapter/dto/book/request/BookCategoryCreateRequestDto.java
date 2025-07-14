package com.nhnacademy.frontend.common.adapter.dto.book.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookCategoryCreateRequestDto {

    @NotBlank
    @Size(max = 100)
    private String categoryName;

    private Long parentId;
}
