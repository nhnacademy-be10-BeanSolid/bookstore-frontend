package com.nhnacademy.frontend.admin.domain.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class BookUpdateRequestDto {

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotBlank
    private String description;

    private String toc;

    @NotBlank
    @Size(max = 255)
    private String publisher;

    @NotBlank
    @Size(max = 255)
    private String author;

    @NotNull
    private LocalDate publishAt;

    @Positive
    private Integer originalPrice;

    @Positive
    private Integer salePrice;

    @NotNull
    private Boolean wrappable;

    @PositiveOrZero
    private Integer stock;

    @NotNull
    private String status;
}
