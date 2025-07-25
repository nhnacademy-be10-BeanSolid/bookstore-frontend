package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.common.adapter.dto.book.response.BookSearchResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class BookSearchController {

    private final BookService bookService;

    @GetMapping("/admin/books-search")
    public BookSearchResponseDto searchAladinBooksJson(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") Integer start,
            @RequestParam(name = "MaxResults", defaultValue = "10") Integer maxResults) {

        return bookService.getBookSearchResponseDto(query, start, maxResults);
    }
}
