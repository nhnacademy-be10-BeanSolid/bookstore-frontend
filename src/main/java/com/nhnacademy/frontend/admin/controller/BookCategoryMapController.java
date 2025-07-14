package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.common.adapter.dto.book.request.BookCategoryMapCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookCategoryMapResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/books/{bookId}/categories")
public class BookCategoryMapController {

    private final BookService bookService;

    @GetMapping
    public String categoryManage(@PathVariable("bookId") Long bookId, Model model) {
        BookCategoryMapResponseDto response = bookService.getBookCategoryMap(bookId);
        model.addAttribute("book", response);
        return "admin/book/category-manage";
    }

    @PostMapping
    public String addCategoryFromBook(@PathVariable("bookId") Long bookId, @ModelAttribute BookCategoryMapCreateRequestDto request) {
        bookService.createBookCategoryMap(bookId, request);
        log.debug("Create category from book success - bookId {}, categoryId {}" , bookId, request.getCategoryId());
        return "redirect:/admin/books/" + bookId + "/categories";
    }

    @DeleteMapping("/{categoryId}")
    public String removeCategoryFromBook(@PathVariable("bookId") Long bookId, @PathVariable("categoryId") Long categoryId) {
        bookService.deleteBookCategoryMap(bookId, categoryId);
        log.debug("Remove category from book success - bookId {}, categoryId {}" , bookId, categoryId);
        return "redirect:/admin/books/" + bookId + "/categories";
    }
}
