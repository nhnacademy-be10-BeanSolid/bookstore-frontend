package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.admin.dto.response.BookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "bookAdminAdaptor")
public interface BookAdminAdaptor {

    @GetMapping("/book-api/admin/books/all")
    List<BookResponse> getAllBooks();
}
