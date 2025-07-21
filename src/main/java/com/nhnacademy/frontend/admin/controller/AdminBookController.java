package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.common.adapter.dto.book.request.BookCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookDetailResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.request.BookUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.service.BookService;
import com.nhnacademy.frontend.common.adapter.dto.book.response.SimpleBookResponseDto;
import com.nhnacademy.frontend.common.exception.ValidationFailedException;
import com.nhnacademy.frontend.coupon.dto.CouponPolicyResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/books")
public class AdminBookController {

    private final BookService bookService;
    private final CouponAdapter couponAdapter;

    // 등록 폼
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("showCreateForm");
        model.addAttribute("book", new BookCreateRequestDto());
        return "admin/book/create-form";
    }

    // 업데이트 폼
    @GetMapping("/{bookId}/edit")
    public String showUpdateForm(@PathVariable("bookId") Long bookId, Model model) {
        log.info("Show update form");
        BookDetailResponseDto book = bookService.getBookDetail(bookId);

        model.addAttribute("book", book);
        model.addAttribute("bookId", bookId);

        return "admin/book/update-form";
    }

    // 도서 리스트
    @GetMapping
    public String getBookList(Pageable pageable, Model model) {
        Page<SimpleBookResponseDto> bookList = bookService.getAllBooks(pageable);
        List<CouponPolicyResponse> couponPolicies = couponAdapter.getAllCouponPolicies();
        log.info("BookListGet Success- page : {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        model.addAttribute("books", bookList.getContent());
        model.addAttribute("page", bookList);
        model.addAttribute("couponPolicies", couponPolicies);
        return "admin/book/book-list";
    }

    // 상세 정보
    @GetMapping("/{bookId}")
    public String getBookDetail(@PathVariable("bookId") Long bookId, Model model) {
        BookDetailResponseDto bookDetail = bookService.getBookDetail(bookId);
        log.info("BookDetail Get Success : {}", bookId);
        model.addAttribute("book", bookDetail);
        return "admin/book/detail";
    }

    // 도서 등록
    @PostMapping
    public String createBook(@Valid @ModelAttribute BookCreateRequestDto request,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        log.info("createBook : {}", request);
        BookResponseDto book = bookService.createBook(request);
        log.info("Book Create Success : {}", book.id());
        return "redirect:/admin/books/" + book.id();
    }

    // 도서 업데이트
    @PutMapping("/{bookId}")
    public String updateBook(@PathVariable("bookId") Long bookId,
                             @Valid @ModelAttribute BookUpdateRequestDto request,
                             BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new ValidationFailedException(bindingResult);
        }
        bookService.updateBook(bookId, request);
        log.info("Book Update Success : {}", bookId);
        return "redirect:/admin/books/" + bookId;
    }

    // 도서 삭제
    @DeleteMapping("/{bookId}")
    public String deleteBook(@PathVariable("bookId") Long bookId) {
        bookService.deleteBook(bookId);
        log.info("Book Delete Success : {}", bookId);
        return "redirect:/admin/books";
    }

    @PostMapping("/associate-coupon")
    public String associateCouponToBook(@RequestParam Long bookId, @RequestParam Long couponPolicyId, RedirectAttributes redirectAttributes) {
        try {
            couponAdapter.issueCouponToBook(couponPolicyId, bookId);
            redirectAttributes.addFlashAttribute("message", "쿠폰이 도서에 성공적으로 연결되었습니다.");
        } catch (Exception e) {
            log.error("Failed to associate coupon {} with book {}: {}", couponPolicyId, bookId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "쿠폰 연결 중 오류가 발생했습니다: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }
}
