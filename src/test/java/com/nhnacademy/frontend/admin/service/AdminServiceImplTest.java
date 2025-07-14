package com.nhnacademy.frontend.admin.service;

import com.nhnacademy.frontend.admin.adapter.UserAdminAdapter;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.common.adapter.dto.user.response.ResponsePointType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    UserAdminAdapter userAdminAdapter;

    @InjectMocks
    AdminServiceImpl adminService;

    @Test
    void getAllPointTypes_returnsPage() {
        Page<ResponsePointType> page = new PageImpl<>(Collections.emptyList());
        when(userAdminAdapter.getAllPointTypes(anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(page));

        Page<ResponsePointType> result = adminService.getAllPointTypes(Pageable.ofSize(10));
        assertThat(result).isNotNull();
        verify(userAdminAdapter).getAllPointTypes(anyInt(), anyInt());
    }

    @Test
    void addPointType_callsAdapter() {
        PointTypeCreateRequestDto dto = new PointTypeCreateRequestDto("type", 1, 1, "grade", true);
        adminService.addPointType(dto);
        verify(userAdminAdapter).addPointType(dto);
    }

    @Test
    void deletePointType_callsAdapter() {
        adminService.deletePointType(1L);
        verify(userAdminAdapter).deletePointType(1L);
    }

    @Test
    void changeActive_callsAdapter() {
        adminService.changeActive(2L);
        verify(userAdminAdapter).changeIsActivePointType(2L);
    }

    @Test
    void updatePointType_callsAdapter() {
        PointTypeUpdateRequestDto dto = new PointTypeUpdateRequestDto("type", 1, 1, "grade");
        adminService.updatePointType(3L, dto);
        verify(userAdminAdapter).editPointType(dto, 3L);
    }

    @Test
    void getPointType_returnsResponse() {
        ResponsePointType response = new ResponsePointType();
        when(userAdminAdapter.getPointType(4L)).thenReturn(ResponseEntity.ok(response));

        ResponsePointType result = adminService.getPointType(4L);
        assertThat(result).isEqualTo(response);
        verify(userAdminAdapter).getPointType(4L);
    }
}
