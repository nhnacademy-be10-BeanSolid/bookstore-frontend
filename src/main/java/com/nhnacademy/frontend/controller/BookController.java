package com.nhnacademy.frontend.controller;

import com.nhnacademy.frontend.book.domain.requset.BookCreateRequestDto;
import com.nhnacademy.frontend.book.domain.response.BookDetailResponseDto;
import com.nhnacademy.frontend.book.domain.response.BookResponseDto;
import com.nhnacademy.frontend.book.domain.requset.BookUpdateRequestDto;
import com.nhnacademy.frontend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    // 등록 폼
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("showCreateForm");
        model.addAttribute("book", new BookCreateRequestDto(null, null, null, null, null, null, null, null, null, null, null, null, Set.of()));
        return "book/create-form";
    }

    // 업데이트 폼
    @GetMapping("/{bookId}/edit")
    public String showUpdateForm(@PathVariable("bookId") Long bookId, Model model) {
        log.info("Show update form");
        BookDetailResponseDto book = bookService.getBookDetail(bookId);

        model.addAttribute("book", book);
        model.addAttribute("bookId", bookId);

        return "book/update-form";
    }

    // 책 리스트
    @GetMapping
    public String getBookList(Pageable pageable, Model model) {
        Page<BookResponseDto> bookList = bookService.getAllBooks(pageable);
        log.info("BookListGet Success- page : {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("books", bookList.getContent());
        model.addAttribute("page", bookList);
        return "book/book-list";
    }

    // 상세 정보
    @GetMapping("/{bookId}")
    public String getBookDetail(@PathVariable("bookId") Long bookId, Model model) {
        BookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);
        log.info("BookDetail Get Success : {}", bookId);
        log.info("Image {}", bookDetail.image());
        model.addAttribute("book", bookDetail);
        return "book/detail";
    }

    // 도서 등록
    @PostMapping
    public String createBook(@ModelAttribute BookCreateRequestDto request) {
        BookResponseDto book = bookService.createBook(request);
        log.info("Book Create Success : {}", book.id());
        return "redirect:/books/" + book.id();
    }

    // 도서 업데이트
    @PutMapping("/{bookId}")
    public String updateBook(@PathVariable Long bookId, @ModelAttribute BookUpdateRequestDto request) {
        bookService.updateBook(bookId, request);
        log.info("Book Update Success : {}", bookId);
        return "redirect:/books/" + bookId;
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        log.info("Book Delete Success : {}", bookId);
        return "redirect:/books";
    }
}
