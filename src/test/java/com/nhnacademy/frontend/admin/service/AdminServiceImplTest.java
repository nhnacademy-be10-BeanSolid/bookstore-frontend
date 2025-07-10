package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.adapter.AdminAdapter;
import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    AdminAdapter adminAdapter;

    @InjectMocks
    AdminServiceImpl adminService;

    @Test
    void getAllPointTypes_returnsPage() {
        Page<ResponsePointType> page = new PageImpl<>(Collections.emptyList());
        when(adminAdapter.getAllPointTypes(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(page));

        Page<ResponsePointType> result = adminService.getAllPointTypes(Pageable.ofSize(10));
        assertThat(result).isNotNull();
        verify(adminAdapter).getAllPointTypes(anyInt(), anyInt());
    }

    @Test
    void addPointType_callsAdapter() {
        PointTypeCreateRequestDto dto = new PointTypeCreateRequestDto("type", 1, 1, "grade", true);
        adminService.addPointType(dto);
        verify(adminAdapter).addPointType(dto);
    }

    @Test
    void deletePointType_callsAdapter() {
        adminService.deletePointType(1L);
        verify(adminAdapter).deletePointType(1L);
    }

    @Test
    void changeActive_callsAdapter() {
        adminService.changeActive(2L);
        verify(adminAdapter).changeIsActivePointType(2L);
    }

    @Test
    void updatePointType_callsAdapter() {
        PointTypeUpdateRequestDto dto = new PointTypeUpdateRequestDto("type", 1, 1, "grade");
        adminService.updatePointType(3L, dto);
        verify(adminAdapter).editPointType(dto, 3L);
    }

    @Test
    void getPointType_returnsResponse() {
        ResponsePointType response = new ResponsePointType();
        when(adminAdapter.getPointType(4L)).thenReturn(ResponseEntity.ok(response));

        ResponsePointType result = adminService.getPointType(4L);
        assertThat(result).isEqualTo(response);
        verify(adminAdapter).getPointType(4L);
    }
}
