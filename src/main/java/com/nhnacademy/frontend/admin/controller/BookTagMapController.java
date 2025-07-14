package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.common.adapter.dto.book.request.BookTagMapCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookTagMapResponseDto;
import com.nhnacademy.frontend.admin.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/books/{bookId}/tags")
public class BookTagMapController {

    private final BookService bookService;

    @GetMapping
    public String tagManage(@PathVariable("bookId") Long bookId, Model model) {
        BookTagMapResponseDto response = bookService.getBookTagMap(bookId);
        model.addAttribute("book", response);
        return "admin/book/tag-manage";
    }

    @PostMapping
    public String addTagFromBook(@PathVariable("bookId") Long bookId, @ModelAttribute BookTagMapCreateRequestDto request) {
        bookService.createBookTagMap(bookId, request);
        log.debug("Create tag from book success - bookId : {}, tagId : {}", bookId, request.getTagId());
        return "redirect:/admin/books/" + bookId + "/tags";
    }

    @DeleteMapping("/{tagId}")
    public String removeTagFromBook(@PathVariable("bookId") Long bookId, @PathVariable("tagId") Long tagId) {
        bookService.deleteBookTagMap(bookId, tagId);
        log.debug("Remove tag from book success - bookId : {}, tagId : {}", bookId, tagId);
        return "redirect:/admin/books/" + bookId + "/tags";
    }
}
