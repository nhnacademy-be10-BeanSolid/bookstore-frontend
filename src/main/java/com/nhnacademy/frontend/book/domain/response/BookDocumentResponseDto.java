package com.nhnacademy.frontend.book.domain.response;

import java.util.Set;

public record BookDocumentResponseDto (
        String id,
        String title,
        String description,
        String author,
        String publisher,
        String isbn,
        Set<String> tags
){
}