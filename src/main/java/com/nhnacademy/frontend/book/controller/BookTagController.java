package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.book.domain.requset.BookTagCreateRequestDto;
import com.nhnacademy.frontend.book.domain.response.BookTagResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/book-tags")
public class BookTagController {

    private final BookService bookService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("request", new BookTagCreateRequestDto());
        return "booktag/create-form";
    }

    @GetMapping
    public String getAllTags(Pageable pageable, Model model) {
        Page<BookTagResponseDto> tagList = bookService.getAllBookTags(pageable);
        log.info("TagListGet Success- page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("tags", tagList.getContent());
        model.addAttribute("page", tagList);
        return "booktag/tag-list";
    }

    @PostMapping
    public String createBookTag(@ModelAttribute BookTagCreateRequestDto request) {
        bookService.createTag(request);
        log.info("Tag Create Success : {}", request.getTagName());
        return "redirect:/book-tags";
    }

    @DeleteMapping("/{tagId}")
    public String deleteBookTag(@PathVariable Long tagId) {
        bookService.deleteBookTag(tagId);
        log.info("Tag Delete Success : {}", tagId);
        return "redirect:/book-tags";
    }
}
