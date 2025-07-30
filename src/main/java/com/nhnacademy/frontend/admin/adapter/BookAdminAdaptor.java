package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.admin.dto.response.BookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "bookAdminAdaptor")
public interface BookAdminAdaptor {
    //페이징된 도서 목록을 정렬 조건과 함께 조회
    @GetMapping("/book-api/admin/books")
    Page<BookResponse> getAllBooks(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("sort") List<String> sort);
}
