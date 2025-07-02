package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.book.domain.requset.*;
import com.nhnacademy.frontend.book.domain.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@FeignClient(name = "book-api")
public interface BookAdapter {

    // 태그 리스트 조회
    @GetMapping("/book-tags")
    Page<BookTagResponseDto> getAllBookTags(@RequestParam int page, @RequestParam int size);

    // 태그 생성
    @PostMapping("/book-tags")
    BookTagResponseDto createTag(@RequestBody BookTagCreateRequestDto request);

    // 태그 삭제
    @DeleteMapping("/book-tags/{tagId}")
    void deleteTag(@PathVariable Long tagId);

    // 카테고리 단건 조회
    @GetMapping("/categories/{categoryId}")
    BookCategoryResponseDto getBookCategory(@PathVariable Long categoryId);

    // 카테고리 리스트 조회
    @GetMapping("/categories")
    Page<BookCategoryResponseDto> getAllBookCategories(@RequestParam int page, @RequestParam int size);

    // 카테고리 생성
    @PostMapping("/categories")
    BookCategoryResponseDto createCategory(@RequestBody BookCategoryCreateRequestDto request);

    // 업데이트 필요한가?
    @PutMapping("/categories/{categoryId}")
    BookCategoryResponseDto updateCategory(@PathVariable Long categoryId, @RequestBody BookCategoryUpdateRequestDto request);

    // 카테고리 삭제
    @DeleteMapping("/categories/{categoryId}")
    void deleteCategory(@RequestParam Long categoryId);

    // 도서 리스트
    @GetMapping("/books")
    Page<BookResponseDto> getBooks(@RequestParam int page, @RequestParam int size);

    // 도서 상세정보
    @GetMapping("/books/{bookId}")
    BookDetailResponseDto getBookDetail(@PathVariable Long bookId);

    // 도서 생성
    @PostMapping("/books")
    BookResponseDto createBook(@RequestBody BookCreateRequestDto request);

    // 외부 도서 검색
    @GetMapping("/books-search")
    BookSearchResponseDto searchBooks(
            @RequestParam String query,
            @RequestParam Integer start);

    // 도서 업데이트
    @PutMapping("/books/{bookId}")
    BookDetailResponseDto updateBook(@PathVariable Long bookId, @RequestBody BookUpdateRequestDto request);

    // 도서 삭제
    @DeleteMapping("/books/{bookId}")
    void deleteBook(@PathVariable Long bookId);

    // 도서에 태그 추가
    @PostMapping("/books/{bookId}/tags")
    void createBookTagMap(@PathVariable Long bookId, @RequestBody BookTagMapCreateRequestDto request);

    // 도서에서 태그 삭제
    @DeleteMapping("/books/{bookId}/tags/{tagId}")
    void deleteBookTagMap(@PathVariable Long bookId, @PathVariable Long tagId);

    // 도서에 카테고리 추가
    @PostMapping("/books/{bookId}/categories")
    void createBookCategoryMap(@PathVariable Long bookId, @RequestBody BookCategoryMapCreateRequestDto request);

    // 도서에서 카테고리 삭제
    @DeleteMapping("/books/{bookId}/categories/{categoryId}")
    void deleteBookCategoryMap(@PathVariable Long bookId, @PathVariable Long categoryId);
}
