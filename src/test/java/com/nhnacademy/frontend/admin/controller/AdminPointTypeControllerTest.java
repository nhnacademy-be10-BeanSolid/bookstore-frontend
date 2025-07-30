package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.service.AdminService;
import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@WebMvcTest(
        controllers = AdminPointTypeController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
class AdminPointTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminService adminService;

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void PointTypeForm_returnsViewAndModel() throws Exception {
        Page<ResponsePointType> page = new PageImpl<>(Collections.emptyList());
        when(adminService.getAllPointTypes(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/admin/pointtype"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/pointtype/pointTypeForm"))
                .andExpect(model().attributeExists("pointTypes"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerPointType_validRequest_returnsOk() throws Exception {
        PointTypeCreateRequestDto requestDto = new PointTypeCreateRequestDto("적립", 100, 1, "VIP", true);
        doNothing().when(adminService).addPointType(any(PointTypeCreateRequestDto.class));

        mockMvc.perform(post("/admin/pointtype")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deletePointType_returnsOk() throws Exception {
        doNothing().when(adminService).deletePointType(1L);

        mockMvc.perform(delete("/admin/pointtype/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void changeActive_returnsOk() throws Exception {
        doNothing().when(adminService).changeActive(2L);

        mockMvc.perform(put("/admin/pointtype/2/isactive"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editPointType_returnsOk() throws Exception {
        PointTypeUpdateRequestDto requestDto = new PointTypeUpdateRequestDto("수정타입", 200, 2, "GOLD");
        doNothing().when(adminService).updatePointType(eq(3L), any(PointTypeUpdateRequestDto.class));

        mockMvc.perform(put("/admin/pointtype/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDto)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void editPointType_get_returnsViewAndModel() throws Exception {
        ResponsePointType response = new ResponsePointType();
        when(adminService.getPointType(4L)).thenReturn(response);

        mockMvc.perform(get("/admin/pointtype/4/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/pointtype/pointType-edit"))
                .andExpect(model().attributeExists("responsePointType"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void registerPointTypeForm_returnsView() throws Exception {
        mockMvc.perform(get("/admin/pointtype/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/pointtype/pointType-register"));
    }
}
