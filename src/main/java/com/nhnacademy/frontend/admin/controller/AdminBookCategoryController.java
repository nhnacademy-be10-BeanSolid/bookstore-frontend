package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.common.adapter.dto.book.request.BookCategoryCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.request.BookCategoryUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookCategoryResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class AdminBookCategoryController {

    private final BookService bookService;

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("request", new BookCategoryCreateRequestDto());
        return "admin/category/create-form";
    }

    @GetMapping("/{categoryId}/edit")
    public String showUpdateForm(@PathVariable("categoryId") Long categoryId, Model model) {
        BookCategoryResponseDto category = bookService.getCategory(categoryId);
        model.addAttribute("category", category);
        model.addAttribute("request", new BookCategoryUpdateRequestDto());
        return "admin/category/update-form";
    }

    @GetMapping("/{categoryId}")
    public String getCategory(Model model, @PathVariable("categoryId") Long categoryId) {
        BookCategoryResponseDto category = bookService.getCategory(categoryId);
        log.debug("Category Get Success : {}", category);
        model.addAttribute("category", category);
        return "admin/category/detail";
    }

    @GetMapping
    public String getAllCategories(Pageable pageable, Model model) {
        Page<BookCategoryResponseDto> categoryList = bookService.getAllBookCategories(pageable);
        log.debug("CategoryListGet Success- page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("categories", categoryList.getContent());
        model.addAttribute("page", categoryList);
        return "admin/category/category-list";
    }

    @PostMapping
    public String createCategory(@Valid @ModelAttribute BookCategoryCreateRequestDto request,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        BookCategoryResponseDto response = bookService.createCategory(request);
        log.debug("Category Create Success : {}", request.getCategoryName());
        return "redirect:/admin/categories/" + response.categoryId();
    }

    @PutMapping("/{categoryId}")
    public String updateCategory(@PathVariable("categoryId") Long categoryId,
                                 @Valid @ModelAttribute BookCategoryUpdateRequestDto request,
                                 BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        bookService.updateCategory(categoryId, request);
        log.debug("Category Update Success : {}", categoryId);
        return "redirect:/admin/categories/" + categoryId;
    }

    @DeleteMapping("/{categoryId}")
    public String deleteCategory(@PathVariable("categoryId") Long categoryId) {
        bookService.deleteCategory(categoryId);
        log.debug("Category Delete Success : {}", categoryId);
        return "redirect:/admin/categories";
    }
}
