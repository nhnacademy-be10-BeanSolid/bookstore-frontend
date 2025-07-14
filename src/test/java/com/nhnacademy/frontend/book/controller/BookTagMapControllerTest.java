package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.admin.controller.BookTagMapController;
import com.nhnacademy.frontend.common.adapter.dto.book.request.BookTagMapCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookTagMapResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookTagResponseDto;
import com.nhnacademy.frontend.book.service.BookService;
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

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BookTagMapController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class BookTagMapControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    void tagManage() throws Exception {
        BookTagResponseDto tag1 = new BookTagResponseDto(1L, "태그1");
        BookTagResponseDto tag2 = new BookTagResponseDto(2L, "태그2");
        BookTagMapResponseDto response = new BookTagMapResponseDto(1L, List.of(tag1, tag2));

        when(bookService.getBookTagMap(1L)).thenReturn(response);

        mockMvc.perform(get("/admin/books/1/tags"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/book/tag-manage"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    void addTagFromBook_success() throws Exception {
        Long bookId = 1L;
        Long tagId = 2L;

        mockMvc.perform(post("/admin/books/1/tags")
                        .param("tagId", String.valueOf(tagId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/" + bookId + "/tags"));

        verify(bookService, times(1)).createBookTagMap(eq(bookId), any(BookTagMapCreateRequestDto.class));
    }

    @Test
    void removeTagFromBook() throws Exception {
        doNothing().when(bookService).deleteBookTagMap(1L,1L);

        mockMvc.perform(delete("/admin/books/1/tags/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books/1/tags"));

        verify(bookService, times(1)).deleteBookTagMap(1L,1L);
    }
}
