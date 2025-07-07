package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.domain.requset.BookTagMapCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.BookDetailResponseDto;
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
        BookDetailResponseDto response = bookService.getBookDetail(bookId);
        model.addAttribute("book", response);
        return "/admin/book/tag-manage";
    }

    @PostMapping
    public String createTagFromBook(@PathVariable("bookId") Long bookId, @ModelAttribute BookTagMapCreateRequestDto request) {
        bookService.createBookTagMap(bookId, request);
        log.debug("Create tag from book success - bookId : {}, tagId : {}", bookId, request.tagId());
        return "redirect:/admin/books/" + bookId + "/tags";
    }

    @DeleteMapping("/{tagId}")
    public String removeTagFromBook(@PathVariable("bookId") Long bookId, @PathVariable("tagId") Long tagId) {
        bookService.deleteBookTagMap(bookId, tagId);
        log.debug("Remove tag from book success - bookId : {}, tagId : {}", bookId, tagId);
        return "redirect:/admin/books/" + bookId + "/tags";
    }
}
