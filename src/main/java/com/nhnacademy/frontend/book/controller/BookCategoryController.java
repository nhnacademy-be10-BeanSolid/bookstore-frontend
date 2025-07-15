package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.common.adapter.dto.book.response.SimpleBookResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class BookCategoryController {

    private final BookService bookService;

    @GetMapping("/{categoryId}")
    public String bookCategory(@PathVariable("categoryId") Long categoryId,
                               @PageableDefault(size = 4) Pageable pageable,
                               Model model) {
        Page<SimpleBookResponseDto> bookList = bookService.getAllBooks(categoryId, pageable);
        List<com.nhnacademy.frontend.book.domain.response.BookCategoryNodeResponseDto> categoryTree = bookService.getCategoryTree();
        model.addAttribute("books", bookList.getContent());
        model.addAttribute("categoryTree", categoryTree);
        model.addAttribute("page", bookList);
        model.addAttribute("basePath", "/categories/" + categoryId);
        log.info("Page : {}", pageable.getPageNumber());
        log.info("Size : {}", pageable.getPageSize());
        return "home";
    }
}
