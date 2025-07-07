package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.domain.requset.BookTagCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.BookTagResponseDto;
import com.nhnacademy.frontend.admin.service.BookService;
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
@RequestMapping("/admin/tags")
public class BookTagController {

    private final BookService bookService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("request", new BookTagCreateRequestDto(null));
        return "admin/tag/create-form";
    }

    @GetMapping
    public String getAllTags(Pageable pageable, Model model) {
        Page<BookTagResponseDto> tagList = bookService.getAllBookTags(pageable);
        log.debug("TagListGet Success- page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("tags", tagList.getContent());
        model.addAttribute("page", tagList);
        return "admin/tag/tag-list";
    }

    @PostMapping
    public String createBookTag(@ModelAttribute BookTagCreateRequestDto request) {
        bookService.createTag(request);
        log.debug("Tag Create Success : {}", request.tagName());
        return "redirect:/admin/tags";
    }

    @DeleteMapping("/{tagId}")
    public String deleteBookTag(@PathVariable("tagId") Long tagId) {
        bookService.deleteBookTag(tagId);
        log.debug("Tag Delete Success : {}", tagId);
        return "redirect:/admin/tags";
    }
}
