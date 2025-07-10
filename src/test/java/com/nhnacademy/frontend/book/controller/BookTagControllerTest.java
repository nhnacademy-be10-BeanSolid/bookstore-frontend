package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.controller.BookTagController;
import com.nhnacademy.frontend.admin.domain.request.BookTagCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.BookTagResponseDto;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BookTagController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class BookTagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void showCreateForm() throws Exception {
        mockMvc.perform(get("/admin/tags/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/tag/create-form"));
    }

    @Test
    void getAllTags() throws Exception {
        BookTagResponseDto tag1 = new BookTagResponseDto(1L, "태그1");
        BookTagResponseDto tag2 = new BookTagResponseDto(2L, "태그2");

        List<BookTagResponseDto> tags = List.of(tag1, tag2);
        Page<BookTagResponseDto> page = new PageImpl<>(tags);

        when(bookService.getAllBookTags(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/tags")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/tag/tag-list"))
                .andExpect(model().attributeExists("tags"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("tags", tags));

        verify(bookService, times(1)).getAllBookTags(any(Pageable.class));
    }

    @Test
    void createBookTag_Success() throws Exception {
        BookTagResponseDto response = new BookTagResponseDto(1L, "테스트");

        when(bookService.createTag(any(BookTagCreateRequestDto.class))).thenReturn(response);

        mockMvc.perform(post("/admin/tags")
                .param("tagName", "테스트"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tags"));

        verify(bookService, times(1)).createTag(any());
    }

    @Test
    void createBookTag_ValidationFail() throws Exception {
        mockMvc.perform(post("/admin/tags")
                .param("tagName", ""))
                .andExpect(view().name("error/error"))
                .andExpect(model().attribute("statusCode", 400));
    }

    @Test
    void deleteBookTag() throws Exception {
        doNothing().when(bookService).deleteBookTag(1L);

        mockMvc.perform(delete("/admin/tags/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/tags"));

        verify(bookService, times(1)).deleteBookTag(1L);
    }
}
