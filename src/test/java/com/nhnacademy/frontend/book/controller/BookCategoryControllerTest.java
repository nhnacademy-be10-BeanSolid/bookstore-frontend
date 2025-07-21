package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookCategoryNodeResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.SimpleBookResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BookCategoryController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class BookCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void bookCategory() throws Exception {
        // 카테고리를 가지고 있는 도서
        SimpleBookResponseDto response1 = new SimpleBookResponseDto(1L, "제목", "작가", 3000, 20, null, 1L, 0L, 0.0);
        SimpleBookResponseDto response2 = new SimpleBookResponseDto(2L, "제목", "작가", 3000, 20, null, 1L, 0L, 0.0);

        List<SimpleBookResponseDto> books = List.of(response1, response2);
        Page<SimpleBookResponseDto> page = new PageImpl<>(books);

        // 카테고리 트리 정보
        BookCategoryNodeResponseDto child1 = new BookCategoryNodeResponseDto(2L, "추리소설", new ArrayList<>());
        BookCategoryNodeResponseDto child2 = new BookCategoryNodeResponseDto(3L, "공포소설", new ArrayList<>());
        BookCategoryNodeResponseDto root = new BookCategoryNodeResponseDto(1L, "소설", List.of(child1, child2));

        BookCategoryNodeResponseDto root1 = new BookCategoryNodeResponseDto(4L, "만화", new ArrayList<>());

        when(bookService.getCategoryTree()).thenReturn(List.of(root, root1));
        when(bookService.getAllBooks(eq(1L), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/categories/1")
                        .param("page", "0")
                        .param("size", "4"))
                .andExpect(status().isOk())
                .andExpect(view().name("home"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("categoryTree"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("basePath"));
    }
}
