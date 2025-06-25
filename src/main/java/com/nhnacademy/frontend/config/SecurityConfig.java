package com.nhnacademy.frontend.config;

import com.nhnacademy.frontend.filter.JwtAuthenticationFilter;
import com.nhnacademy.frontend.filter.LoginFilter;
import com.nhnacademy.frontend.handler.CustomCookieClearingLogoutHandler;
import com.nhnacademy.frontend.handler.LoginSuccessHandler;
import com.nhnacademy.frontend.service.AuthService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   AuthService authService,
                                                   LoginSuccessHandler successHandler,
                                                   CustomCookieClearingLogoutHandler customCookieClearingLogoutHandler) throws Exception {

        LoginFilter loginFilter = new LoginFilter("/auth/login", authService, successHandler, new SimpleUrlAuthenticationFailureHandler());
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                )
                .logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .addLogoutHandler(customCookieClearingLogoutHandler)
                        .logoutSuccessUrl("/")
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAt(loginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
