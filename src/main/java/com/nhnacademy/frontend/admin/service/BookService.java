package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.domain.requset.*;
import com.nhnacademy.frontend.admin.domain.response.*;
import com.nhnacademy.frontend.book.domain.response.SimpleBookResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookTagResponseDto createTag(BookTagCreateRequestDto request);

    Page<BookTagResponseDto> getAllBookTags(Pageable pageable);

    void deleteBookTag(Long tagId);

    BookCategoryResponseDto createCategory(BookCategoryCreateRequestDto request);

    BookCategoryResponseDto getCategory(Long id);

    Page<BookCategoryResponseDto> getAllBookCategories(Pageable pageable);

    BookCategoryResponseDto updateCategory(Long id, BookCategoryUpdateRequestDto request);

    void deleteCategory(Long id);

    Page<SimpleBookResponseDto> getAllBooks(Pageable pageable);

    BookDetailResponseDto getBookDetail(Long id);

    BookResponseDto createBook(BookCreateRequestDto request);

    BookSearchResponseDto searchNaverBooks(String query, Integer start);

    BookDetailResponseDto updateBook(Long id, BookUpdateRequestDto request);

    void deleteBook(Long id);

    void createBookTagMap(Long bookId, BookTagMapCreateRequestDto request);

    void deleteBookTagMap(Long bookId, Long tagId);

    void createBookCategoryMap(Long bookId, BookCategoryMapCreateRequestDto request);

    void deleteBookCategoryMap(Long bookId, Long tagId);

    void createBookLike(Long bookId, String tagId);

    void deleteBookLike(Long bookId, String userId);
}
