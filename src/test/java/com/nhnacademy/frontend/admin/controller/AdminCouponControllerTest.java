package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.adapter.BookAdminAdaptor;
import com.nhnacademy.frontend.admin.adapter.CategoryAdminAdaptor;
import com.nhnacademy.frontend.admin.adapter.CouponAdminAdaptor;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@ActiveProfiles("test")
@WebMvcTest(
        controllers = AdminCouponController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AdminCouponControllerTest {

    @Autowired
    MockMvc mockMvc;


    @MockBean
    private RedisConnectionFactory redisConnectionFactory;


    /* ======= 실제로 사용하는 Mock ======= */
    @MockBean CouponAdminAdaptor   couponAdminAdaptor;
    @MockBean BookAdminAdaptor     bookAdminAdaptor;
    @MockBean CategoryAdminAdaptor categoryAdminAdaptor;

    /* ------------------------ 테스트 메서드 ------------------------ */

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void showCouponList() throws Exception {
        var dto = mock(com.nhnacademy.frontend.admin.dto.response.CouponPolicyResponse.class);
        given(couponAdminAdaptor.getAllCouponPolicies()).willReturn(List.of(dto));

        mockMvc.perform(get("/admin/coupons"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/coupon/coupon_list"))
                .andExpect(model().attribute("couponPolicies", List.of(dto)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void showCreateCouponForm() throws Exception {
        var book = mock(com.nhnacademy.frontend.admin.dto.response.BookResponse.class);
        given(bookAdminAdaptor.getAllBooks(eq(0), anyInt(), any()))
                .willReturn(new PageImpl<>(List.of(book)));

        var cat = mock(com.nhnacademy.frontend.admin.dto.response.BookCategoryResponse.class);
        given(categoryAdminAdaptor.getAllCategories()).willReturn(List.of(cat));

        mockMvc.perform(get("/admin/coupons/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/coupon/create_coupon_form"))
                .andExpect(model().attributeExists(
                        "couponPolicyCreateRequest", "allBooks", "allCategories"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createCouponPolicy() throws Exception {
        mockMvc.perform(post("/admin/coupons/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("couponName", "테스트쿠폰")
                        .param("discountAmount", "2000"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/coupons?success=true"));

        var captor = ArgumentCaptor.forClass(
                com.nhnacademy.frontend.admin.dto.request.CouponPolicyCreateRequest.class);
        verify(couponAdminAdaptor).createCouponPolicy(captor.capture());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void showCouponDetail() throws Exception {
        var dto = mock(com.nhnacademy.frontend.admin.dto.response.CouponPolicyResponse.class);
        given(couponAdminAdaptor.getCouponPolicyById(5L)).willReturn(dto);

        mockMvc.perform(get("/admin/coupons/5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/coupon/coupon_detail"))
                .andExpect(model().attribute("couponPolicy", dto));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteCouponPolicy() throws Exception {
        mockMvc.perform(post("/admin/coupons/7/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/coupons?deleted=true"));

        verify(couponAdminAdaptor).deleteCouponPolicy(7L);
    }
}