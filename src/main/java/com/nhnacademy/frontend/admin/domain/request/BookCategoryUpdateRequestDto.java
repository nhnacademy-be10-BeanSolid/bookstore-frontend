package com.nhnacademy.frontend.admin.domain.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BookCategoryUpdateRequestDto {

    @NotBlank
    @Size(max = 100)
    private String categoryName;

    private Long parentId;
}