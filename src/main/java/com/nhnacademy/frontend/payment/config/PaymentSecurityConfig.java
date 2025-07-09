package com.nhnacademy.frontend.payment.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Order(1)   // 전역 설정보다 먼저 적용되도록 순서 변경
public class PaymentSecurityConfig {
    @Bean
    public SecurityFilterChain paymentSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/payments/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/payments",
                                "/payments/form",
                                "/payments/success",
                                "/payments/fail")
                        .permitAll()
                        .requestMatchers(HttpMethod.POST, "/payments").permitAll()
                        .anyRequest().permitAll()
                )
                .csrf(csrf -> csrf.disable());
        return http.build();
    }
}