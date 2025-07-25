package com.nhnacademy.frontend.common.config;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.filter.LoginFilter;
import com.nhnacademy.frontend.auth.handler.CustomCookieClearingLogoutHandler;
import com.nhnacademy.frontend.auth.handler.LoginFailureHandler;
import com.nhnacademy.frontend.auth.handler.LoginSuccessHandler;
import com.nhnacademy.frontend.auth.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private static final String LOGIN_URL = "/auth/login";

    @Bean
    public LoginFailureHandler loginFailureHandler() {
        return new LoginFailureHandler();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   AuthService authService,
                                                   LoginSuccessHandler successHandler,
                                                   LoginFailureHandler failureHandler,
                                                   CustomCookieClearingLogoutHandler customCookieClearingLogoutHandler) throws Exception {

        LoginFilter loginFilter = new LoginFilter(
                LOGIN_URL, authService, successHandler,
                failureHandler
        );

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.GET,
                                "/payments",
                                "/payments/form",
                                "/payments/form/**",
                                "/payments/success",
                                "/payments/fail"
                        ).permitAll()
                        .requestMatchers(HttpMethod.POST,
                                "/payments"
                        ).permitAll()
                        // 인증 없이 열어둘 경로들
                        .requestMatchers("/reviews/book/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .requestMatchers("/webjars/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers(HttpMethod.POST, "/orders").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/orders/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/orders/*/input-detail").permitAll()
                        .requestMatchers(HttpMethod.GET, "/orders/non-member-detail").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/cart/**").permitAll()
                        .requestMatchers("/books/**").permitAll()
                        .requestMatchers("/images/**").permitAll()
                        .requestMatchers("/reviews/**").permitAll()
                        .requestMatchers("/search/**").permitAll()
                        .requestMatchers("/categories/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl(LOGIN_URL)
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(customCookieClearingLogoutHandler)
                        .logoutSuccessUrl("/")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        return http.build();
    }
}
