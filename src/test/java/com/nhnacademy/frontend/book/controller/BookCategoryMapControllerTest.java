package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.controller.BookCategoryMapController;
import com.nhnacademy.frontend.common.adapter.dto.book.request.BookCategoryMapCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookCategoryMapResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookCategoryResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(
        controllers = BookCategoryMapController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class BookCategoryMapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void categoryManage() throws Exception {
        BookCategoryResponseDto category1 = new BookCategoryResponseDto(1L, "카테고리1",
                null, null, null, null);
        BookCategoryResponseDto category2 = new BookCategoryResponseDto(2L, "카테고리2",
                null, null, null, null);
        BookCategoryMapResponseDto response = new BookCategoryMapResponseDto(1L, List.of(category1, category2));

        when(bookService.getBookCategoryMap(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/books/1/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book/category-manage"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void addCategoryFromBook() throws Exception {
        Long bookId = 1L;
        Long categoryId = 2L;

        mockMvc.perform(post("/admin/books/1/categories")
                        .param("categoryId", String.valueOf(categoryId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/" + bookId + "/categories"));

        verify(bookService, times(1)).createBookCategoryMap(eq(bookId), any(BookCategoryMapCreateRequestDto.class));
    }

    @Test
    void removeCategoryFromBook() throws Exception {
        doNothing().when(bookService).deleteBookCategoryMap(1L, 1L);

        mockMvc.perform(delete("/admin/books/1/categories/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/1/categories"));

        verify(bookService, times(1)).deleteBookCategoryMap(1L,1L);
    }
}
