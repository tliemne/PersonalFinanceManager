package com.example.PersonalFinanceManager.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class test {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // tắt CSRF cho Postman
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // cho phép tất cả request

        return http.build();
    }
}
