package com.nhnacademy.frontend.book.service;

import com.nhnacademy.frontend.book.domain.BookTagCreateRequestDto;
import com.nhnacademy.frontend.book.domain.BookTagResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BookService {
    BookTagResponseDto createTag(BookTagCreateRequestDto request);

    Page<BookTagResponseDto> getAllBookTags(Pageable pageable);

    BookTagResponseDto updateTag(Long tagId, BookTagCreateRequestDto request);

    void deleteBookTag(Long tagId);
}
