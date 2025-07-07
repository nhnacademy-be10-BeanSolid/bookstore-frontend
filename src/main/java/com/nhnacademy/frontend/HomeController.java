package com.nhnacademy.frontend;

import com.nhnacademy.frontend.admin.service.BookService;
import com.nhnacademy.frontend.book.domain.response.SimpleBookResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class HomeController {

    private final BookService bookService;

    @GetMapping
    public String home(@PageableDefault(size = 4) Pageable pageable, Model model) {
        Page<SimpleBookResponseDto> bookList = bookService.getAllBooks(pageable);
        log.debug("BookListGet Success- page : {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("books", bookList.getContent());
        model.addAttribute("page", bookList);
        return "home";
    }
}
