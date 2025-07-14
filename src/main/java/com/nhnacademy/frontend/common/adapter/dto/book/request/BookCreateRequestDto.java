package com.nhnacademy.frontend.common.adapter.dto.book.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class BookCreateRequestDto {

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

    @NotBlank
    @Pattern(regexp = "^.{13}$")
    private String isbn;

    @Positive
    private int originalPrice;

    @Positive
    private int salePrice;

    @NotNull
    private boolean wrappable;

    @PositiveOrZero
    private int stock;

    private String image;

    @NotEmpty
    private Set<Long> categoryIds;
}