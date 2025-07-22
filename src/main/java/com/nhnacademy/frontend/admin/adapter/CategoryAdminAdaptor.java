package com.nhnacademy.frontend.admin.adapter;

import com.nhnacademy.frontend.admin.dto.response.BookCategoryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "gateway-service", contextId = "categoryAdminAdaptor")
public interface CategoryAdminAdaptor {

    @GetMapping("/book-api/categories/all")
    List<BookCategoryResponse> getAllCategories();
}
