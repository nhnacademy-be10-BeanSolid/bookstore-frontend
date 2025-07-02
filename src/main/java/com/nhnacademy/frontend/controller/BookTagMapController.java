package com.nhnacademy.frontend.controller;

import com.nhnacademy.frontend.book.domain.requset.BookTagMapCreateRequestDto;
import com.nhnacademy.frontend.book.domain.response.BookDetailResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books/{bookId}/tags")
public class BookTagMapController {

    private final BookService bookService;

    @GetMapping
    public String tagManage(@PathVariable("bookId") Long bookId, Model model) {
        BookDetailResponseDto response = bookService.getBookDetail(bookId);
        model.addAttribute("book", response);
        return "book/tag-manage";
    }

    @PostMapping
    public String createTagMap(@PathVariable("bookId") Long bookId, @ModelAttribute BookTagMapCreateRequestDto request) {
        log.info("start");
        bookService.createBookTagMap(bookId, request);
        return "redirect:/books/" + bookId + "/tags";
    }

    @DeleteMapping("/{categoryId}")
    public String removeTagFromBook(@PathVariable Long bookId, @PathVariable Long categoryId) {
        bookService.deleteBookTagMap(bookId, categoryId);
        log.info("Remove tag from book success - bookId : {}, tagId : {}", bookId, categoryId);
        return "redirect:/books/" + bookId;
    }
}
