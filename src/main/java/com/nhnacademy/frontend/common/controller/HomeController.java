package com.nhnacademy.frontend.common.controller;

import com.nhnacademy.frontend.book.domain.response.BookCategoryNodeResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
import com.nhnacademy.frontend.common.adapter.dto.book.response.SimpleBookResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping
    public String home(@PageableDefault(size = 4) Pageable pageable, Model model) {
        Page<SimpleBookResponseDto> bookList = bookService.getAllBooks(pageable);
        log.info("BookListGet Success - page : {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        List<BookCategoryNodeResponseDto> categoryTree = bookService.getCategoryTree();
        log.info("Sort : {}", pageable.getSort());
        model.addAttribute("books", bookList.getContent());
        model.addAttribute("categoryTree", categoryTree);
        model.addAttribute("page", bookList);
        return "home";
    }

}
