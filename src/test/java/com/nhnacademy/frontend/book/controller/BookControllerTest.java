package com.nhnacademy.frontend.book.controller;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.common.adapter.CouponAdapter;
import com.nhnacademy.frontend.common.adapter.UserAdapter;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookCategoryResponseDto;
import com.nhnacademy.frontend.common.adapter.dto.book.response.BookDetailResponseDto;
import com.nhnacademy.frontend.common.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        controllers = BookController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BookService bookService;

    @MockBean
    CouponAdapter couponAdapter;

    @MockBean
    UserAdapter userAdapter;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("GET /books/{bookId} - 도서 상세 페이지 반환")
    void testBookDetail() throws Exception {
        Long bookId = 1L;
        BookCategoryResponseDto category = new BookCategoryResponseDto(1L, "카테고리", null, null, LocalDateTime.now(), null);
        BookDetailResponseDto detail = new BookDetailResponseDto(
                1L, "제목", "설명", null, "출판사",
                "작가", LocalDate.of(2020,1,1), "1234567891011",
                3000, 1000, true, LocalDateTime.now(), null, "ON_SALE", 30,  null, List.of(category), null, 0);

        given(bookService.getBookDetail(bookId)).willReturn(detail);

        mockMvc.perform(get("/books/{bookId}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("book/book-detail"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("POST /books - 주문 요청 후 /orders 리다이렉트")
    void testBookOrders() throws Exception {
        mockMvc.perform(post("/books")
                        .param("bookId", "1")
                        .param("title", "테스트 책")
                        .param("salePrice", "9000")
                        .param("wrappable", "true")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"))
                .andExpect(flash().attributeExists("bookId", "title", "salePrice", "wrappable", "quantity"));
    }

}
