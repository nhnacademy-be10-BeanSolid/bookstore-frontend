package com.nhnacademy.frontend.book.service;

import com.nhnacademy.frontend.common.adapter.BookAdapter;
import com.nhnacademy.frontend.admin.domain.request.*;
import com.nhnacademy.frontend.admin.domain.response.*;
import com.nhnacademy.frontend.admin.service.impl.BookServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookAdapter bookAdapter;

    @InjectMocks
    private BookServiceImpl bookService;

    @Test
    @DisplayName("태그 생성 - 성공")
    void createTag_Success() {
        BookTagCreateRequestDto request = new BookTagCreateRequestDto();
        request.setTagName("테스트");
        BookTagResponseDto response = new BookTagResponseDto(1L, "테스트");

        when(bookAdapter.createTag(request)).thenReturn(response);

        BookTagResponseDto result = bookService.createTag(request);

        assertThat(result).isNotNull();
        assertThat(result.tagId()).isEqualTo(1L);
        assertThat(result.tagName()).isEqualTo("테스트");

        verify(bookAdapter, times(1)).createTag(request);
    }

    @Test
    @DisplayName("태그 리스트 페이징 - 성공")
    void getAllBooks_Success() {
        BookTagResponseDto response = new BookTagResponseDto(1L, "테스트");
        BookTagResponseDto response1 = new BookTagResponseDto(2L, "테스트1");
        BookTagResponseDto response2 = new BookTagResponseDto(3L, "테스트2");
        Page<BookTagResponseDto> page = new PageImpl<>(List.of(response, response1, response2));

        when(bookAdapter.getAllBookTags(0, 20)).thenReturn(page);

        Page<BookTagResponseDto> result = bookService.getAllBookTags(PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);

        verify(bookAdapter, times(1)).getAllBookTags(0, 20);
    }

    @Test
    @DisplayName("태그 삭제 - 성공")
    void deleteBookTag_Success() {
        doNothing().when(bookAdapter).deleteTag(1L);

        bookService.deleteBookTag(1L);

        verify(bookAdapter, times(1)).deleteTag(1L);
    }

    @Test
    @DisplayName("카테고리 생성 - 성공")
    void createCategory_Success() {
        BookCategoryCreateRequestDto request = new BookCategoryCreateRequestDto();
        request.setCategoryName("테스트");
        request.setParentId(null);
        BookCategoryResponseDto response = new BookCategoryResponseDto(1L, "테스트",
                null, null, LocalDateTime.now(), null);

        when(bookAdapter.createCategory(request)).thenReturn(response);

        BookCategoryResponseDto result = bookService.createCategory(request);

        assertThat(result).isNotNull();
        assertThat(result.categoryId()).isEqualTo(1L);
        assertThat(result.categoryName()).isEqualTo("테스트");
        assertThat(result.parentId()).isNull();
        assertThat(result.createdAt()).isNotNull();

        verify(bookAdapter, times(1)).createCategory(request);
    }

    @Test
    @DisplayName("카테고리 단건 조회 - 성공")
    void getCategory_Success() {
        BookCategoryResponseDto response = new BookCategoryResponseDto(1L, "테스트",
                null, null, LocalDateTime.now(), null);

        when(bookAdapter.getBookCategory(1L)).thenReturn(response);

        BookCategoryResponseDto result = bookService.getCategory(1L);

        assertThat(result).isNotNull();
        assertThat(result.categoryId()).isEqualTo(1L);
        assertThat(result.categoryName()).isEqualTo("테스트");
        assertThat(result.parentId()).isNull();
        assertThat(result.createdAt()).isNotNull();

        verify(bookAdapter, times(1)).getBookCategory(1L);
    }

    @Test
    @DisplayName("카테고리 리스트 페이징 - 성공")
    void getAllCategories_Success() {
        BookCategoryResponseDto response = new BookCategoryResponseDto(1L, "테스트",
                null, null, LocalDateTime.now(), null);
        BookCategoryResponseDto response1 = new BookCategoryResponseDto(2L, "테스트 자식",
                1L, "테스트", LocalDateTime.now(), null);
        Page<BookCategoryResponseDto> page = new PageImpl<>(List.of(response, response1));

        when(bookAdapter.getAllBookCategories(0, 20)).thenReturn(page);

        Page<BookCategoryResponseDto> result = bookService.getAllBookCategories(PageRequest.of(0, 20));

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).categoryName()).isEqualTo("테스트");
        assertThat(result.getContent().get(1).categoryName()).isEqualTo("테스트 자식");

        verify(bookAdapter, times(1)).getAllBookCategories(0, 20);
    }

    @Test
    @DisplayName("카테고리 업데이트 - 성공")
    void updateCategory_Success() {
        BookCategoryUpdateRequestDto request = new BookCategoryUpdateRequestDto();
        request.setCategoryName("수정 테스트");
        BookCategoryResponseDto response = new BookCategoryResponseDto(1L, "수정 테스트",
                null, null, LocalDateTime.now(), LocalDateTime.now());

        when(bookAdapter.updateCategory(1L, request)).thenReturn(response);

        BookCategoryResponseDto result = bookService.updateCategory(1L, request);

        assertThat(result).isNotNull();
        assertThat(result.categoryId()).isEqualTo(1L);
        assertThat(result.categoryName()).isEqualTo("수정 테스트");
        assertThat(result.updatedAt()).isNotNull();

        verify(bookAdapter, times(1)).updateCategory(1L, request);
    }

    @Test
    @DisplayName("카테고리 삭제 - 성공")
    void deleteCategory_Success() {
        doNothing().when(bookAdapter).deleteCategory(1L);

        bookService.deleteCategory(1L);

        verify(bookAdapter, times(1)).deleteCategory(1L);
    }

//    @Test
//    @DisplayName("도서 생성 - 성공")
//    void createBook_Success() {
//        BookCreateRequestDto request = new BookCreateRequestDto();
//        request.setTitle("제목");
//        request.setDescription("설명");
//        request.setPublisher("출판사");
//        request.setAuthor("작가");
//        request.setPublishAt(LocalDate.of(2020, 1, 1));
//        request.setIsbn("1234567891011"); // 13 자리
//        request.setOriginalPrice(3000);
//        request.setSalePrice(1000);
//        request.setWrappable(Boolean.TRUE);
//        request.setStock(100);
//        request.setCategoryIds(Set.of(1L));
//
//        BookResponseDto response = new BookResponseDto(1L, "제목", "설명", null, "출판사",
//                "작가", LocalDate.of(2020,1,1), "1234567891011", 3000, 1000, true, "ON_SALE", 30,  null, Set.of("카테고리1, 카테고리2"), null);
//
//        when(bookAdapter.createBook(request)).thenReturn(response);
//
//        BookResponseDto result = bookService.createBook(request);
//
//        assertNotNull(result);
//    }

    @Test
    @DisplayName("도서 태그 추가 - 성공")
    void createBookTagMap_Success() {
        BookTagMapCreateRequestDto request = new BookTagMapCreateRequestDto();
        request.setTagId(1L);

        bookService.createBookTagMap(1L, request);

        verify(bookAdapter, times(1)).createBookTagMap(1L, request);
    }

    @Test
    @DisplayName("도서 태그 삭제 - 성공")
    void deleteBookTagMap_Success() {
        bookService.deleteBookCategoryMap(1L, 1L);

        verify(bookAdapter, times(1)).deleteBookCategoryMap(1L, 1L);
    }

    @Test
    @DisplayName("도서의 태그 조회 - 성공")
    void getBookTagMap_Success() {
        BookTagResponseDto tag1 = new BookTagResponseDto(1L, "태그1");
        BookTagResponseDto tag2 = new BookTagResponseDto(2L, "태그2");
        BookTagMapResponseDto response = new BookTagMapResponseDto(1L, List.of(tag1, tag2));

        when(bookAdapter.getBookTagMap(1L)).thenReturn(response);

        BookTagMapResponseDto result = bookService.getBookTagMap(1L);

        assertThat(result.bookId()).isEqualTo(1L);
        assertThat(result.tags()).hasSize(2);
        assertThat(result.tags()).extracting("tagId")
                .containsExactly(1L, 2L);
        assertThat(result.tags()).extracting("tagName")
                .containsExactly("태그1", "태그2");
    }

    @Test
    @DisplayName("도서에 카테고리 추가 - 성공")
    void createBookCategoryMap_Success() {
        BookCategoryMapCreateRequestDto request = new BookCategoryMapCreateRequestDto();
        request.setCategoryId(1L);

        bookService.createBookCategoryMap(1L, request);

        verify(bookAdapter, times(1)).createBookCategoryMap(1L, request);
    }

    @Test
    @DisplayName("도서 태그 삭제 - 성공")
    void deleteBookCategoryMap_Success() {
        bookService.deleteBookCategoryMap(1L, 1L);

        verify(bookAdapter, times(1)).deleteBookCategoryMap(1L, 1L);
    }

    @Test
    @DisplayName("도서의 태그 조회 - 성공")
    void getBookCategoryMap_Success() {
        BookCategoryResponseDto category1 = new BookCategoryResponseDto(1L, "카테고리1",
                null, null, LocalDateTime.now(), null);
        BookCategoryResponseDto category2 = new BookCategoryResponseDto(2L, "카테고리2",
                null, null, LocalDateTime.now(), null);
        BookCategoryMapResponseDto response = new BookCategoryMapResponseDto(1L, List.of(category1, category2));

        when(bookAdapter.getBookCategoryMap(1L)).thenReturn(response);

        BookCategoryMapResponseDto result = bookService.getBookCategoryMap(1L);

        assertThat(result.bookId()).isEqualTo(1L);
        assertThat(result.categories()).hasSize(2);
        assertThat(result.categories()).extracting("categoryId")
                .containsExactly(1L, 2L);
        assertThat(result.categories()).extracting("categoryName")
                .containsExactly("카테고리1", "카테고리2");
    }
}
