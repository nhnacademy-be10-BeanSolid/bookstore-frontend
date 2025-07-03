package com.nhnacademy.frontend.common.error;

import com.nhnacademy.frontend.common.advice.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Mock
    Model model;

    @Test
    void handleException_withJsonError_setsModelAttributes() {
        // given
        String exceptionMessage = "{\"status\":404,\"error\":\"Not Found\"}";
        Exception e = new Exception(exceptionMessage);

        // when
        String view = handler.handleException(e, model);

        // then
        assertEquals("error/error", view);
        verify(model).addAttribute(eq("statusCode"), eq(404));
        verify(model).addAttribute(eq("userFriendlyMessage"), eq("페이지를 찾을 수 없습니다."));
    }
}
