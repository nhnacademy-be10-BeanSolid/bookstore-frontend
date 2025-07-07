package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.domain.response.BookDetailResponseDto;
import com.nhnacademy.frontend.admin.domain.response.BookLikeResponse;
import com.nhnacademy.frontend.admin.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    public String bookDetail(@PathVariable("bookId") Long bookId,  Model model) {
        BookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);
        model.addAttribute("book", bookDetail);
        return "/book/book-detail";
    }
}
