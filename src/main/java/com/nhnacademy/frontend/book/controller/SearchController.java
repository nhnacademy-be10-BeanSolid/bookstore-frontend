package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.domain.response.BookTagMapResponseDto;
import com.nhnacademy.frontend.book.domain.response.SimpleBookResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/search")
public class SearchController {

    private final BookService bookService;

    @GetMapping
    public String searchBooks(@RequestParam String keyword, Pageable pageable, Model model) {
        log.info("searchBooks keyword = {}", keyword);
        Page<SimpleBookResponseDto> result = bookService.elasticSearchBooks(keyword, pageable);
        model.addAttribute("books", result.getContent());
        model.addAttribute("page", result);
        model.addAttribute("keyword", keyword);
        return "search";
    }
}