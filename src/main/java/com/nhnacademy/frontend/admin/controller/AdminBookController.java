package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.domain.request.BookCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.BookDetailResponseDto;
import com.nhnacademy.frontend.admin.domain.response.BookResponseDto;
import com.nhnacademy.frontend.admin.domain.request.BookUpdateRequestDto;
import com.nhnacademy.frontend.book.service.BookService;
import com.nhnacademy.frontend.book.domain.response.SimpleBookResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;

    // 등록 폼
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("showCreateForm");
        model.addAttribute("book", new BookCreateRequestDto());
        return "admin/book/create-form";
    }

    // 업데이트 폼
    @GetMapping("/{bookId}/edit")
    public String showUpdateForm(@PathVariable("bookId") Long bookId, Model model) {
        log.info("Show update form");
        BookDetailResponseDto book = bookService.getBookDetail(bookId);

        model.addAttribute("book", book);
        model.addAttribute("bookId", bookId);

        return "admin/book/update-form";
    }

    // 도서 리스트
    @GetMapping
    public String getBookList(Pageable pageable, Model model) {
        Page<SimpleBookResponseDto> bookList = bookService.getAllBooks(pageable);
        log.info("BookListGet Success- page : {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("books", bookList.getContent());
        model.addAttribute("page", bookList);
        return "admin/book/book-list";
    }

    // 상세 정보
    @GetMapping("/{bookId}")
    public String getBookDetail(@PathVariable("bookId") Long bookId, Model model) {
        BookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);
        log.info("BookDetail Get Success : {}", bookId);
        model.addAttribute("book", bookDetail);
        return "admin/book/detail";
    }

    // 도서 등록
    @PostMapping
    public String createBook(@ModelAttribute BookCreateRequestDto request) {
        log.info("createBook : {}", request);
        BookResponseDto book = bookService.createBook(request);
        log.info("Book Create Success : {}", book.id());
        return "redirect:/admin/books/" + book.id();
    }

    // 도서 업데이트
    @PutMapping("/{bookId}")
    public String updateBook(@PathVariable("bookId") Long bookId, @ModelAttribute BookUpdateRequestDto request) {
        bookService.updateBook(bookId, request);
        log.info("Book Update Success : {}", bookId);
        return "redirect:/admin/books/" + bookId;
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        log.info("Book Delete Success : {}", bookId);
        return "redirect:/admin/books";
    }
}
