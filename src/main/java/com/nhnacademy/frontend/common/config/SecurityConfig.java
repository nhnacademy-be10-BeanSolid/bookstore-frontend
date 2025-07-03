package com.nhnacademy.frontend.common.config;

import com.nhnacademy.frontend.auth.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.auth.filter.LoginFilter;
import com.nhnacademy.frontend.auth.handler.CustomCookieClearingLogoutHandler;
import com.nhnacademy.frontend.auth.handler.LoginSuccessHandler;
import com.nhnacademy.frontend.auth.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    private static final String LOGIN_URL = "/auth/login";

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   AuthService authService,
                                                   LoginSuccessHandler successHandler,
                                                   CustomCookieClearingLogoutHandler customCookieClearingLogoutHandler) throws Exception {
        LoginFilter loginFilter = new LoginFilter(LOGIN_URL, authService, successHandler, new SimpleUrlAuthenticationFailureHandler());
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage(LOGIN_URL)
                        .loginProcessingUrl(LOGIN_URL))
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
