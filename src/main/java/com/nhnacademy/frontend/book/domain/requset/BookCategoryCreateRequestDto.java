package com.nhnacademy.frontend.book.domain.requset;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookCategoryCreateRequestDto {
    String categoryName;
    Long parentId;
}
