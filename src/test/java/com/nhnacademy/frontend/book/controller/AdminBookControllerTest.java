package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.controller.AdminBookController;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookResponseDto;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = AdminBookController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
public class AdminBookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/admin/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book/create-form"));
    }

    @Test
    void getBookList() throws Exception {
        SimpleBookResponseDto response1 = new SimpleBookResponseDto(1L, "제목", "작가", 3000, 20, null, 1L);
        SimpleBookResponseDto response2 = new SimpleBookResponseDto(2L, "제목", "작가", 3000, 20, null, 1L);

        List<SimpleBookResponseDto> books = List.of(response1, response2);
        Page<SimpleBookResponseDto> page = new PageImpl<>(books);

        when(bookService.getAllBooks(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book/book-list"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attributeExists("page"));

        verify(bookService, times(1)).getAllBooks(any(Pageable.class));
    }

    @Test
    void createBook_Success() throws Exception {
        BookResponseDto response = new BookResponseDto(1L, "제목", "설명", null, "출판사",
                "작가", LocalDate.of(2020,1,1), "1234567891011", 3000, 1000, true, "ON_SALE", 30,  null, Set.of("카테고리1, 카테고리2"), null);
        when(bookService.createBook(any())).thenReturn(response);

        mockMvc.perform(post("/admin/books")
                        .param("title", "테스트책")
                        .param("description", "설명")
                        .param("author", "작가")
                        .param("publisher", "출판사")
                        .param("publishAt", "2025-01-01")
                        .param("isbn", "9788998756514")
                        .param("originalPrice", "15000")
                        .param("salePrice", "12000")
                        .param("wrappable", "true")
                        .param("stock", "100")
                        .param("categoryIds", "1", "2")
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/" + response.id()));

        verify(bookService, times(1)).createBook(any());
    }

    @Test
    void createBook_ValidationFail() throws Exception {
        mockMvc.perform(post("/admin/books")
                .param("title", ""))
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("statusCode", 400));
    }

    @Test
    void deleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/admin/books/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books"));

        verify(bookService, times(1)).deleteBook(1L);
    }
}

