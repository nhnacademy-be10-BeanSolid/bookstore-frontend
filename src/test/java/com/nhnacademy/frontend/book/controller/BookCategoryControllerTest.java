package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.controller.BookCategoryController;
import com.nhnacademy.frontend.admin.domain.request.BookCategoryCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.request.BookCategoryUpdateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.BookCategoryResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/admin/categories/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/create-form"));
    }

    @Test
    void showEditForm() throws Exception {
        BookCategoryResponseDto response = new BookCategoryResponseDto(
                1L, "테스트", null, null,
                LocalDateTime.now(), null);

        when(bookService.getCategory(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/categories/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/update-form"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attributeExists("request"));
    }

    @Test
    void getCategory() throws Exception {
        BookCategoryResponseDto response = new BookCategoryResponseDto(
                1L,"테스트", null, null,
                LocalDateTime.now(), null
        );

        when(bookService.getCategory(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/categories/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/detail"))
                .andExpect(model().attributeExists("category"));
    }

    @Test
    void getAllCategories() throws Exception {
        BookCategoryResponseDto response = new BookCategoryResponseDto(
                1L, "테스트", null, null,
                LocalDateTime.now(), null
        );
        BookCategoryResponseDto response1 = new BookCategoryResponseDto(
                2L, "테스트 자식", null, null,
                LocalDateTime.now(), null
        );

        List<BookCategoryResponseDto> categories = List.of(response, response1);
        Page<BookCategoryResponseDto> page = new PageImpl<>(categories);

        when(bookService.getAllBookCategories(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/categories")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category/category-list"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attributeExists("page"));

        verify(bookService, times(1)).getAllBookCategories(any(Pageable.class));
    }

    @Test
    void createCategory_Success() throws Exception {
        BookCategoryResponseDto response = new BookCategoryResponseDto(
                1L, "테스트", null, null,
                LocalDateTime.now(), null
        );

        when(bookService.createCategory(any(BookCategoryCreateRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/admin/categories")
                .param("categoryName", "테스트"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories/1"));

        verify(bookService, times(1)).createCategory(any(BookCategoryCreateRequestDto.class));
    }

    @Test
    void createBookCategory_ValidationFail() throws Exception {
        mockMvc.perform(post("/admin/categories")
                .param("categoryName", ""))
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("statusCode", 400));
    }

    @Test
    void updateCategory_Success() throws Exception {
        BookCategoryResponseDto response = new BookCategoryResponseDto(
                1L, "수정 테스트", null, null,
                LocalDateTime.of(2020, 1, 1, 0, 0), LocalDateTime.now()
        );

        when(bookService.updateCategory(eq(1L), any(BookCategoryUpdateRequestDto.class)))
                .thenReturn(response);

        mockMvc.perform(put("/admin/categories/1")
                        .param("categoryName", "수정 테스트"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories/1"));

        verify(bookService, times(1)).updateCategory(eq(1L), any(BookCategoryUpdateRequestDto.class));
    }

    @Test
    void updateCategory_ValidationFail() throws Exception {
        mockMvc.perform(post("/admin/categories")
                        .param("categoryName", ""))
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("statusCode", 400));
    }

    @Test
    void deleteBookCategory() throws Exception {
        doNothing().when(bookService).deleteCategory(1L);

        mockMvc.perform(delete("/admin/categories/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/categories"));

        verify(bookService, times(1)).deleteCategory(1L);
    }
}
