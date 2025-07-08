package com.nhnacademy.frontend.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(2)   // 전역(SecurityConfig)의 다음 순서로 적용
public class PaymentSecurityConfig {

    @Bean
    public SecurityFilterChain paymentSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 이 체인은 /payments/** 에만 적용
                .securityMatcher("/payments/**")
                .authorizeHttpRequests(authorize -> authorize
                        // GET 요청들은 모두 허용
                        .requestMatchers(HttpMethod.GET,
                                "/payments",
                                "/payments/form",
                                "/payments/success",
                                "/payments/fail")
                        .permitAll()
                        // POST /payments(실제 결제 요청)만 인증 필요
                        .requestMatchers(HttpMethod.POST, "/payments")
                        .authenticated()
                        // 그 외에도 혹시 있을 수 있는 /payments/** 는 인증
                        .anyRequest().authenticated()
                )
                // CSRF 꺼두기 (API 호출 시 편의)
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}