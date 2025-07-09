package com.nhnacademy.frontend.common.adapter;

import com.nhnacademy.frontend.cart.dto.response.BookResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "bookAdapter")
public interface BookAdapter {
    @GetMapping("/book-api/books/ids")
    List<BookResponse> getBooks(@RequestParam List<Long> ids);
}
