package com.nhnacademy.frontend.book.service;

import com.nhnacademy.frontend.book.adapter.BookAdapter;
import com.nhnacademy.frontend.admin.domain.request.*;
import com.nhnacademy.frontend.admin.domain.response.*;
import com.nhnacademy.frontend.book.domain.response.BookCategoryNodeResponseDto;
import com.nhnacademy.frontend.book.domain.response.SimpleBookResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public BookCategoryResponseDto getCategory(Long categoryId) {
        log.info("Category Get Start : {}", categoryId);
        return bookAdapter.getBookCategory(categoryId);
    }

    @Override
    public Page<BookCategoryResponseDto> getAllBookCategories(Pageable pageable) {
        log.info("CategoryListGet Start - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return bookAdapter.getAllBookCategories(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public BookCategoryResponseDto updateCategory(Long categoryId, BookCategoryUpdateRequestDto request) {
        log.info("Category Update Start : {}", categoryId);
        return bookAdapter.updateCategory(categoryId, request);
    }

    @Override
    public void deleteCategory(Long categoryId) {
        log.debug("Category Delete Start : {}", categoryId);
        bookAdapter.deleteCategory(categoryId);
    }

    // 여기서 시작
    @Override
    public Page<SimpleBookResponseDto> getAllBooks(Pageable pageable) {
        log.info("BookList Get Start - page: {}, size: {}, sort: {}",
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort());

        // 정렬 정보 추출
        String sortStr = null;
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortStr = order.getProperty() + "," + order.getDirection().name().toLowerCase();
        }

        return bookAdapter.getBooks(pageable.getPageNumber(), pageable.getPageSize(), sortStr);
    }

    @Override
    public BookDetailResponseDto getBookDetail(Long bookId) {
        log.info("BookDetail Get Start : {}", bookId);
        return bookAdapter.getBookDetail(bookId);
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
    public BookDetailResponseDto updateBook(Long bookId, BookUpdateRequestDto request) {
        log.info("Book Update Start : {}", bookId);
        return bookAdapter.updateBook(bookId, request);
    }

    @Override
    public void deleteBook(Long bookId) {
        log.debug("Book Delete Start : {}", bookId);
        bookAdapter.deleteBook(bookId);
    }

    @Override
    public void createBookTagMap(Long bookId, BookTagMapCreateRequestDto request) {
        log.info("BookTagMap Create Start - bookId {}", bookId);
        bookAdapter.createBookTagMap(bookId, request);
    }

    @Override
    public BookTagMapResponseDto getBookTagMap(Long bookId) {
        log.info("BookTagMap Get Start - bookId {}", bookId);
        return bookAdapter.getBookTagMap(bookId);
    }

    @Override
    public void deleteBookTagMap(Long bookId, Long tagId) {
        log.info("BookTagMap Delete Start - bookId: {}, tagId: {}", bookId, tagId);
        bookAdapter.deleteBookTagMap(bookId, tagId);
    }

    // 시작
    @Override
    public void createBookCategoryMap(Long bookId, BookCategoryMapCreateRequestDto request) {
        log.info("BookCategoryMap Create Start - bookId {}", bookId);
        bookAdapter.createBookCategoryMap(bookId, request);
    }

    @Override
    public BookCategoryMapResponseDto getBookCategoryMap(Long bookId) {
        log.info("BookCategoryMap Get Start - bookId {}", bookId);
        return bookAdapter.getBookCategoryMap(bookId);
    }

    @Override
    public void deleteBookCategoryMap(Long bookId, Long tagId) {
        log.info("BookCategoryMap Delete Start - bookId: {}, tagId: {}", bookId, tagId);
        bookAdapter.deleteBookCategoryMap(bookId, tagId);
    }

    @Override
    public void createBookLike(Long bookId, String userId) {
        log.info("BookLike Create Start - bookId: {}, tagId: {}", bookId, userId);
        bookAdapter.createBookLike(bookId, userId);
    }

    @Override
    public void deleteBookLike(Long bookId, String userId) {
        log.info("BookLike Delete Start - bookId: {}, userId: {}", bookId, userId);
        bookAdapter.deleteBookLike(bookId, userId);
    }

    @Override
    public Page<SimpleBookResponseDto> elasticSearchBooks(String keyword, Pageable pageable) {
        log.info("Book Search Start - keyword: {}", keyword);
        return bookAdapter.searchBooks(keyword, pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public List<BookCategoryNodeResponseDto> getCategoryTree(){
        return bookAdapter.getCategoryTree();
    }
}
