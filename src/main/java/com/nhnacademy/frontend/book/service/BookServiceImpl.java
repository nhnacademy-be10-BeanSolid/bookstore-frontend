package com.nhnacademy.frontend.book.service;

import com.nhnacademy.frontend.adapter.BookAdapter;
import com.nhnacademy.frontend.book.domain.requset.*;
import com.nhnacademy.frontend.book.domain.response.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookAdapter bookAdapter;

    @Override
    public BookTagResponseDto createTag(BookTagCreateRequestDto request) {
        log.info("Tag Create Start : {}", request.getTagName());
        return bookAdapter.createTag(request);
    }

    @Override
    public Page<BookTagResponseDto> getAllBookTags(Pageable pageable) {
        log.info("TagListGet Start - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return bookAdapter.getAllBookTags(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public void deleteBookTag(Long tagId) {
        log.info("Tag Delete Start : {}", tagId);
        bookAdapter.deleteTag(tagId);
    }

    @Override
    public BookCategoryResponseDto createCategory(BookCategoryCreateRequestDto request) {
        log.info("Category Create Start : {}", request.getCategoryName());
        return bookAdapter.createCategory(request);
    }

    @Override
    public BookCategoryResponseDto getCategory(Long id) {
        log.info("Category Get Start : {}", id);
        return bookAdapter.getBookCategory(id);
    }

    @Override
    public Page<BookCategoryResponseDto> getAllBookCategories(Pageable pageable) {
        log.info("CategoryListGet Start - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return bookAdapter.getAllBookCategories(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public BookCategoryResponseDto updateCategory(Long id, BookCategoryUpdateRequestDto request) {
        log.info("Category Update Start : {}", id);
        return bookAdapter.updateCategory(id, request);
    }

    @Override
    public void deleteCategory(Long id) {
        log.debug("Category Delete Start : {}", id);
        bookAdapter.deleteCategory(id);
    }

    @Override
    public Page<BookResponseDto> getAllBooks(Pageable pageable) {
        log.info("BookList Get Start - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return bookAdapter.getBooks(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public BookDetailResponseDto getBookDetail(Long id) {
        log.info("BookDetail Get Start : {}", id);
        return bookAdapter.getBookDetail(id);
    }

    @Override
    public BookResponseDto createBook(BookCreateRequestDto request) {
        log.info("Book Create Start");
        return bookAdapter.createBook(request);
    }

    @Override
    public BookSearchResponseDto searchNaverBooks(String query, Integer start) {
        log.info("Search Start - query: {}, start: {}", query, start);
        return bookAdapter.searchBooks(query, start);
    }

    @Override
    public BookDetailResponseDto updateBook(Long id, BookUpdateRequestDto request) {
        log.info("Book Update Start : {}", id);
        return bookAdapter.updateBook(id, request);
    }

    @Override
    public void deleteBook(Long id) {
        log.debug("Book Delete Start : {}", id);
        bookAdapter.deleteBook(id);
    }

    @Override
    public void createBookTagMap(Long bookId, BookTagMapCreateRequestDto request) {
        log.info("BookTagMap Create Start - bookId {}", bookId);
        bookAdapter.createBookTagMap(bookId, request);
    }

    @Override
    public void deleteBookTagMap(Long bookId, Long tagId) {
        log.info("BookTagMap Delete Start - bookId: {}, tagId: {}", bookId, tagId);
        bookAdapter.deleteBookTagMap(bookId, tagId);
    }

    @Override
    public void createBookCategoryMap(Long bookId, BookCategoryMapCreateRequestDto request) {
        log.info("BookCategoryMap Create Start - bookId {}", bookId);
        bookAdapter.createBookCategoryMap(bookId, request);
    }

    @Override
    public void deleteBookCategoryMap(Long bookId, Long tagId) {
        log.info("BookCategoryMap Delete Start - bookId: {}, tagId: {}", bookId, tagId);
        bookAdapter.deleteBookCategoryMap(bookId, tagId);
    }
}
