package com.nhnacademy.frontend.common.service;

import com.nhnacademy.frontend.common.adapter.dto.book.request.*;
import com.nhnacademy.frontend.common.adapter.dto.book.response.*;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookSearchResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookTagResponseDto createTag(BookTagCreateRequestDto request);

    Page<BookTagResponseDto> getAllBookTags(Pageable pageable);

    void deleteBookTag(Long tagId);

    BookCategoryResponseDto createCategory(BookCategoryCreateRequestDto request);

    BookCategoryResponseDto getCategory(Long categoryId);

    Page<BookCategoryResponseDto> getAllBookCategories(Pageable pageable);

    BookCategoryResponseDto updateCategory(Long categoryId, BookCategoryUpdateRequestDto request);

    void deleteCategory(Long id);

    Page<SimpleBookResponseDto> getAllBooks(Pageable pageable);

    Page<SimpleBookResponseDto> getAllBooks(Long categoryId, Pageable pageable);

    BookDetailResponseDto getBookDetail(Long bookId);

    BookDetailResponseDto getAdminBookDetail(Long bookId);

    BookResponseDto createBook(BookCreateRequestDto request);

    BookSearchResponseDto getBookSearchResponseDto(String query, Integer start, Integer maxResults);

    BookDetailResponseDto updateBook(Long bookId, BookUpdateRequestDto request);

    void deleteBook(Long bookId);

    void createBookTagMap(Long bookId, BookTagMapCreateRequestDto request);

    BookTagMapResponseDto getBookTagMap(Long bookId);

    void deleteBookTagMap(Long bookId, Long tagId);

    void createBookCategoryMap(Long bookId, BookCategoryMapCreateRequestDto request);

    BookCategoryMapResponseDto getBookCategoryMap(Long bookId);

    void deleteBookCategoryMap(Long bookId, Long tagId);

    void createBookLike(Long bookId, String tagId);

    void deleteBookLike(Long bookId, String userId);

    Page<SimpleBookResponseDto> elasticSearchBooks(String keyword, Pageable pageable);

    List<BookCategoryNodeResponseDto> getCategoryTree();
}
