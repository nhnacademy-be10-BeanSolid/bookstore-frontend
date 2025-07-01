package com.nhnacademy.frontend.controller;

import com.nhnacademy.frontend.book.domain.BookCategoryCreateRequestDto;
import com.nhnacademy.frontend.book.domain.BookCategoryResponseDto;
import com.nhnacademy.frontend.book.domain.BookCategoryUpdateRequestDto;
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
@RequestMapping("/categories")
public class BookCategoryController {

    private final BookService bookService;

    @GetMapping("/new")
    public String showCreateForm() {
        return "bookcategory/create-form";
    }

    @GetMapping("/{categoryId}/edit")
    public String showUpdateForm(@PathVariable("categoryId") Long categoryId, Model model) {
        BookCategoryResponseDto category = bookService.getCategory(categoryId);
        model.addAttribute("category", category);
        model.addAttribute("request", new BookCategoryUpdateRequestDto(category.categoryName(), category.parentId()));
        return "bookcategory/update-form";
    }

    @GetMapping("/{categoryId}")
    public String getCategory(Model model, @PathVariable("categoryId") Long categoryId) {
        BookCategoryResponseDto category = bookService.getCategory(categoryId);
        log.info("Category Get Success : {}", category);
        model.addAttribute("category", category);;
        return "bookcategory/detail";
    }

    @GetMapping
    public String getAllBookCategories(Pageable pageable, Model model) {
        Page<BookCategoryResponseDto> categoryList = bookService.getAllBookCategories(pageable);
        log.info("CategoryListGet Success- page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("categories", categoryList.getContent());
        model.addAttribute("page", categoryList);
        return "bookcategory/category-list";
    }

    @PostMapping
    public String createCategory(@ModelAttribute BookCategoryCreateRequestDto request) {
        BookCategoryResponseDto response = bookService.createCategory(request);
        log.info("Category Create Success : {}", request.getCategoryName());
        return "redirect:/categories/" + response.categoryId();
    }

    @PutMapping("/{categoryId}")
    public String updateCategory(@PathVariable("categoryId") Long categoryId, @ModelAttribute BookCategoryUpdateRequestDto request) {
//        BookCategoryResponseDto response = bookService.updateCategory(categoryId, request);
        log.info("Category Update Success : {}", categoryId);
        return "redirect:/categories/" + categoryId;
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId) {
        bookService.deleteCategory(categoryId);
        log.info("Category Delete Success : {}", categoryId);
        return "redirect:/categories";
    }
}
