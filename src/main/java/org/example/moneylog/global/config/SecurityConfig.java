package org.example.moneylog.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())              // H2 콘솔 폼과 충돌 방지
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))  // H2 콘솔이 프레임을 사용
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());       // JWT 인증 구현 전까지 임시 허용

        return http.build();
    }
}