package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.book.domain.response.BookCategoryNodeResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.SimpleBookResponseDto;
import com.nhnacademy.frontend.cart.dto.response.BookResponse;
import com.nhnacademy.frontend.common.adapter.dto.book.request.*;
import com.nhnacademy.frontend.common.adapter.dto.book.response.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "bookAdapter")
public interface BookAdapter {

    // 태그 리스트 조회
    @GetMapping("/book-api/book-tags")
    Page<BookTagResponseDto> getAllBookTags(@RequestParam int page, @RequestParam int size);

    // 태그 생성
    @PostMapping("/book-api/book-tags")
    BookTagResponseDto createTag(@RequestBody BookTagCreateRequestDto request);

    // 태그 삭제
    @DeleteMapping("/book-api/book-tags/{tagId}")
    void deleteTag(@PathVariable Long tagId);

    // 카테고리 단건 조회
    @GetMapping("/book-api/categories/{categoryId}")
    BookCategoryResponseDto getBookCategory(@PathVariable Long categoryId);

    // 카테고리 리스트 조회
    @GetMapping("/book-api/categories")
    Page<BookCategoryResponseDto> getAllBookCategories(@RequestParam int page, @RequestParam int size);

    // 카테고리 생성
    @PostMapping("/book-api/categories")
    BookCategoryResponseDto createCategory(@RequestBody BookCategoryCreateRequestDto request);

    // 업데이트 필요한가?
    @PutMapping("/book-api/categories/{categoryId}")
    BookCategoryResponseDto updateCategory(@PathVariable Long categoryId, @RequestBody BookCategoryUpdateRequestDto request);

    // 카테고리 삭제
    @DeleteMapping("/book-api/categories/{categoryId}")
    void deleteCategory(@RequestParam Long categoryId);

    // 도서 리스트
    @GetMapping("/book-api/books")
    Page<SimpleBookResponseDto> getBooks(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(name = "sort", required = false) String sort
    );

    // 도서 상세정보
    @GetMapping("/book-api/books/{bookId}")
    BookDetailResponseDto getBookDetail(@PathVariable Long bookId);

    // 도서 생성
    @PostMapping("/book-api/books")
    BookResponseDto createBook(@RequestBody BookCreateRequestDto request);

    // 외부 도서 검색
    @GetMapping("/book-api/books-search")
    BookSearchResponseDto searchBooks(
            @RequestParam String query,
            @RequestParam Integer start);

    // 도서 업데이트
    @PutMapping("/book-api/books/{bookId}")
    BookDetailResponseDto updateBook(@PathVariable Long bookId, @RequestBody BookUpdateRequestDto request);

    // 도서 삭제
    @DeleteMapping("/book-api/books/{bookId}")
    void deleteBook(@PathVariable Long bookId);

    // 해당 도서의 태그 조회
    @GetMapping("/book-api/books/{bookId}/tags")
    BookTagMapResponseDto getBookTagMap(@PathVariable Long bookId);

    // 도서에 태그 추가
    @PostMapping("/book-api/books/{bookId}/tags")
    void createBookTagMap(@PathVariable Long bookId, @RequestBody BookTagMapCreateRequestDto request);

    // 도서에서 태그 삭제
    @DeleteMapping("/book-api/books/{bookId}/tags/{tagId}")
    void deleteBookTagMap(@PathVariable Long bookId, @PathVariable Long tagId);

    // 해당 도서의 카테고리 조회
    @GetMapping("/book-api/books/{bookId}/categories")
    BookCategoryMapResponseDto getBookCategoryMap(@PathVariable Long bookId);

    // 도서에 카테고리 추가
    @PostMapping("/book-api/books/{bookId}/categories")
    void createBookCategoryMap(@PathVariable Long bookId, @RequestBody BookCategoryMapCreateRequestDto request);

    // 도서에서 카테고리 삭제
    @DeleteMapping("/book-api/books/{bookId}/categories/{categoryId}")
    void deleteBookCategoryMap(@PathVariable Long bookId, @PathVariable Long categoryId);

    // 도서 좋아요 생성
    @PostMapping("/book-api/books/{bookId}/bookLikes")
    BookLikeResponse createBookLike(@PathVariable Long bookId, @RequestHeader String userId);

    // 좋아요 조회
    @GetMapping("/book-api/books/{bookId}/bookLikes")
    Page<BookLikeResponse> getBookLikes(@PathVariable Long bookId, @RequestParam int page, @RequestParam int size);

    // 좋아요 삭제
    @DeleteMapping("/book-api/books/{bookId}/bookLikes")
    void deleteBookLike(@PathVariable Long bookId, @RequestHeader String userId);

    // 엘라스틱 서치
    @GetMapping("/book-api/search")
    Page<SimpleBookResponseDto> searchBooks(
            @RequestParam String keyword,
            @RequestParam Integer start,
            @RequestParam Integer size,
            @RequestParam(name = "sort", required = false) String sort);

    @GetMapping("/book-api/books/ids")
    List<BookResponse> getBooks(@RequestParam List<Long> ids);

    @GetMapping("/book-api/categories/tree")
    List<BookCategoryNodeResponseDto> getCategoryTree();
}
