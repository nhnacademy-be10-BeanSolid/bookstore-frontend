package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/bookLikes")
public class BookLikeController {

    private final BookService bookService;

    @PostMapping
    public String createBookLike(@PathVariable Long bookId, @RequestHeader String userId) {
        bookService.createBookLike(bookId, userId);
        return "redirect:/books/" + bookId;
    }


    @DeleteMapping
    public String deleteBookLike(@PathVariable Long bookId, @RequestHeader String userId) {
        bookService.deleteBookLike(bookId, userId);
        return null;
    }
}
