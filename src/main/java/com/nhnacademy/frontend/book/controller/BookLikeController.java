package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/bookLikes")
public class BookLikeController {

    private final BookService bookService;

    @PostMapping
    public String createBookLike(@PathVariable Long bookId, Model model) {
        String userId = (String) model.getAttribute("loginUserId");
        bookService.createBookLike(bookId, userId);
        log.info("BookLike Create Success - bookId: {}, userId: {}", bookId, userId);
        return "redirect:/books/" + bookId;
    }

    @DeleteMapping
    public String deleteBookLike(@PathVariable Long bookId, Model model) {
        String userId = (String) model.getAttribute("loginUserId");
        bookService.deleteBookLike(bookId, userId);
        log.info("BookLike Delete Success - bookId: {}, userId: {}", bookId, userId);
        return "redirect:/books/" + bookId;
    }
}
