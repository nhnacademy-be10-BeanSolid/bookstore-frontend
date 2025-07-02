package com.nhnacademy.frontend.adapter.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Component
public class FeignJwtInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) return;

        HttpServletRequest request = attributes.getRequest();

        String jwtToken = null;
        if(request.getCookies() != null) {
            for(Cookie cookie : request.getCookies()) {
                if("accessToken".equals(cookie.getName())) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if(jwtToken != null) {
            log.debug("Authorization 헤더에 JWT 토큰 추가: {}", jwtToken);
            requestTemplate.header("Authorization", "Bearer " + jwtToken);
        }
    }
}
