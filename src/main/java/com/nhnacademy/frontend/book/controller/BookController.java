package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.domain.response.BookDetailResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @GetMapping("/{bookId}")
    public String bookDetail(@PathVariable("bookId") Long bookId,  Model model) {
        BookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);
        log.info(bookDetail.toString());
        model.addAttribute("book", bookDetail);
        return "book/book-detail";
    }

    // 리다이렉트
    @PostMapping
    public String bookOrders(@RequestParam Long bookId,
                             @RequestParam String title,
                             @RequestParam Integer salePrice,
                             @RequestParam Boolean wrappable,
                             @RequestParam Integer quantity,
                             RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("bookId", bookId);
        redirectAttributes.addFlashAttribute("title", title);
        redirectAttributes.addFlashAttribute("salePrice", salePrice);
        redirectAttributes.addFlashAttribute("wrappable", wrappable);
        redirectAttributes.addFlashAttribute("quantity", quantity);

        return "redirect:/orders";
    }
}
