package com.nhnacademy.frontend.adapter;

import com.nhnacademy.frontend.book.domain.BookTagCreateRequestDto;
import com.nhnacademy.frontend.book.domain.BookTagResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;

@FeignClient(name = "book-api")
public interface BookAdapter {

    @GetMapping("/book-tags")
    Page<BookTagResponseDto> getAllBookTags(@RequestParam int page, @RequestParam int size);

    @PostMapping("/book-tags")
    BookTagResponseDto createTag(@RequestBody BookTagCreateRequestDto request);

    @PutMapping
    BookTagResponseDto updateTag(@RequestParam Long tagId, @RequestBody BookTagCreateRequestDto request);

    @DeleteMapping("/book-tags/{tagId}")
    void deleteTag(@PathVariable Long tagId);

}
