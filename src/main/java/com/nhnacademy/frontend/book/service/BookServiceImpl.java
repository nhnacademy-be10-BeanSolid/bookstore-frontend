package com.nhnacademy.frontend.book.service;

import com.nhnacademy.frontend.adapter.BookAdapter;
import com.nhnacademy.frontend.book.domain.BookTagCreateRequestDto;
import com.nhnacademy.frontend.book.domain.BookTagResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookAdapter bookAdapter;

    @Override
    public BookTagResponseDto createTag(BookTagCreateRequestDto request) {
        log.info("Tag Create Start : {}", request.getTagName());
        return bookAdapter.createTag(request);
    }

    @Override
    public Page<BookTagResponseDto> getAllBookTags(Pageable pageable) {
        log.info("TagListGet Start - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
        return bookAdapter.getAllBookTags(pageable.getPageNumber(), pageable.getPageSize());
    }

    @Override
    public BookTagResponseDto updateTag(Long tagId, BookTagCreateRequestDto request) {
        log.info("Tag Update Start : {}", request.getTagName());
        return bookAdapter.updateTag(tagId, request);
    }

    @Override
    public void deleteBookTag(Long tagId) {
        log.info("Tag Delete Start : {}", tagId);
        bookAdapter.deleteTag(tagId);
    }
}
