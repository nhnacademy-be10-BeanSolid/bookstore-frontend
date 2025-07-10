package com.nhnacademy.frontend.admin.controller;

import com.nhnacademy.frontend.admin.domain.request.PointTypeCreateRequestDto;
import com.nhnacademy.frontend.admin.domain.request.PointTypeUpdateRequestDto;
import com.nhnacademy.frontend.admin.domain.response.ResponsePointType;
import com.nhnacademy.frontend.admin.service.AdminService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class AdminPointTypeControllerTest {

    @Mock
    AdminService adminService;
    @Mock
    Model model;
    @Mock
    BindingResult bindingResult;
    @Mock
    RedirectAttributes redirectAttributes;

    @InjectMocks
    AdminPointTypeController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void PointTypeForm_addsAttributesAndReturnsView() {
        Page<ResponsePointType> page = new PageImpl<>(Collections.emptyList());
        when(adminService.getAllPointTypes(any(Pageable.class))).thenReturn(page);

        String view = controller.PointTypeForm(Pageable.ofSize(10), model);

        verify(model).addAttribute(eq("pointTypes"), any());
        verify(model).addAttribute(eq("page"), eq(page));
        assertThat(view).isEqualTo("admin/pointtype/pointTypeForm");
    }

    @Test
    void registerPointType_validRequest_success() {
        PointTypeCreateRequestDto dto = new PointTypeCreateRequestDto("type", 1, 1, "grade", true);
        when(bindingResult.hasErrors()).thenReturn(false);

        String result = controller.registerPointType(dto, bindingResult, redirectAttributes);

        verify(adminService).addPointType(dto);
        verify(redirectAttributes).addFlashAttribute(eq("registerSuccess"), anyString());
        assertThat(result).isEqualTo("redirect:/admin/pointtype");
    }

    @Test
    void registerPointType_bindingError_throwsException() {
        when(bindingResult.hasErrors()).thenReturn(true);

        org.junit.jupiter.api.Assertions.assertThrows(
                com.nhnacademy.frontend.common.exception.ValidationFailedException.class,
                () -> controller.registerPointType(null, bindingResult, redirectAttributes)
        );
    }

    @Test
    void deletePointType_success() {
        String result = controller.deletePointType(1L, redirectAttributes);
        verify(adminService).deletePointType(1L);
        verify(redirectAttributes).addFlashAttribute(eq("deleteSuccess"), anyString());
        assertThat(result).isEqualTo("redirect:/admin/pointtype");
    }

    @Test
    void changeActive_success() {
        String result = controller.changeActive(2L, redirectAttributes);
        verify(adminService).changeActive(2L);
        verify(redirectAttributes).addFlashAttribute(eq("changeSuccess"), anyString());
        assertThat(result).isEqualTo("redirect:/admin/pointtype");
    }

    @Test
    void editPointType_success() {
        PointTypeUpdateRequestDto dto = new PointTypeUpdateRequestDto("type", 1, 1, "grade");
        String result = controller.editPointType(dto, 3L, redirectAttributes);
        verify(adminService).updatePointType(3L, dto);
        verify(redirectAttributes).addFlashAttribute(eq("editSuccess"), anyString());
        assertThat(result).isEqualTo("redirect:/admin/pointtype");
    }

    @Test
    void editPointType_get_returnsView() {
        ResponsePointType response = new ResponsePointType();
        when(adminService.getPointType(4L)).thenReturn(response);

        String view = controller.editPointType(4L, model);

        verify(model).addAttribute("responsePointType", response);
        assertThat(view).isEqualTo("admin/pointtype/pointType-edit");
    }
}
