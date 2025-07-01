package com.nhnacademy.frontend.book.service;

import com.nhnacademy.frontend.book.domain.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookTagResponseDto createTag(BookTagCreateRequestDto request);

    Page<BookTagResponseDto> getAllBookTags(Pageable pageable);

    void deleteBookTag(Long tagId);

    BookCategoryResponseDto createCategory(BookCategoryCreateRequestDto request);

    BookCategoryResponseDto getCategory(Long id);

    Page<BookCategoryResponseDto> getAllBookCategories(Pageable pageable);

    BookCategoryResponseDto updateCategory(Long id, BookCategoryUpdateRequestDto request);

    void deleteCategory(Long id);

    Page<BookResponseDto> getAllBooks(Pageable pageable);

    BookDetailResponseDto getBookDetail(Long id);

    BookResponseDto createBook(BookCreateRequestDto request);

    BookDetailResponseDto updateBook(Long id, BookUpdateRequestDto request);

    void deleteBook(Long id);
}
