package com.nhnacademy.frontend.common.interceptor;

import com.nhnacademy.frontend.common.adapter.interceptor.FeignJwtInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class FeignJwtInterceptorTest {
    @Mock
    private RequestTemplate requestTemplate;

    private final FeignJwtInterceptor interceptor = new FeignJwtInterceptor();

    @Test
    void whenRequestAttributesAreNull_thenNoHeaderAdded() {
        try(MockedStatic<RequestContextHolder> mockedHolder = mockStatic(RequestContextHolder.class)) {
            mockedHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(null);
            interceptor.apply(requestTemplate);

            verifyNoInteractions(requestTemplate);
        }
    }

    @Test
    void whenNoCookies_thenNoHeaderAdded() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        when(mockRequest.getCookies()).thenReturn(null);

        ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
        try(MockedStatic<RequestContextHolder> mockedHolder = mockStatic(RequestContextHolder.class)) {
            mockedHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attrs);

            interceptor.apply(requestTemplate);

            verifyNoInteractions(requestTemplate);
        }
    }

    @Test
    void whenCookiesDoNotContainAccessToken_thenNoHeaderAdded() {
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Cookie[] cookies = { new Cookie("other", "value"), new Cookie("foo", "bar")};
        when(mockRequest.getCookies()).thenReturn(cookies);

        ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
        try(MockedStatic<RequestContextHolder> mockedHolder = mockStatic(RequestContextHolder.class)) {
            mockedHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attrs);

            interceptor.apply(requestTemplate);

            verifyNoInteractions(requestTemplate);
        }
    }

    @Test
    void whenAccessTokenCookiePresent_thenAuthorizationHeaderAdded() {
        String tokenValue = "abc123";
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        Cookie[] cookies = {
                new Cookie("foo", "bar"),
                new Cookie("accessToken", tokenValue),
                new Cookie("other", "baz")
        };
        when(mockRequest.getCookies()).thenReturn(cookies);

        ServletRequestAttributes attrs = new ServletRequestAttributes(mockRequest);
        try(MockedStatic<RequestContextHolder> mockedHolder = mockStatic(RequestContextHolder.class)) {
            mockedHolder.when(RequestContextHolder::getRequestAttributes).thenReturn(attrs);

            interceptor.apply(requestTemplate);

            verify(requestTemplate, times(1))
                    .header("Authorization", "Bearer " + tokenValue);
        }
    }
}