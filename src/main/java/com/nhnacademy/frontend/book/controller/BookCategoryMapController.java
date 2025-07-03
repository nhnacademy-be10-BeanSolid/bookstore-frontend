package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.book.domain.requset.BookCategoryMapCreateRequestDto;
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
@RequestMapping("/books/{bookId}/categories")
public class BookCategoryMapController {

    private final BookService bookService;

    @GetMapping
    public String categoryManage(@PathVariable Long bookId, Model model) {
        BookDetailResponseDto response = bookService.getBookDetail(bookId);
        model.addAttribute("book", response);
        return "book/category-manage";
    }

    @PostMapping
    public String createCategoryMap(@PathVariable("bookId") Long bookId, @ModelAttribute BookCategoryMapCreateRequestDto request) {
        bookService.createBookCategoryMap(bookId, request);
        return "redirect:/books/" + bookId + "/categories";
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategoryMap(@PathVariable("bookId") Long bookId, @PathVariable("categoryId") Long categoryId) {
        bookService.deleteBookCategoryMap(bookId, categoryId);
        return "redirect:/books/" + bookId + "/categories";
    }
}
