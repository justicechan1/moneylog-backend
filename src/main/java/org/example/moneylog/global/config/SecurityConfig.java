package org.example.moneylog.global.config;

import lombok.RequiredArgsConstructor;
import org.example.moneylog.global.security.JwtAuthenticationFilter;
import org.example.moneylog.global.security.JwtProvider;
import org.example.moneylog.global.security.RestAccessDeniedHandler;
import org.example.moneylog.global.security.RestAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;
    private final RestAuthenticationEntryPoint authenticationEntryPoint; // 401 형식
    private final RestAccessDeniedHandler accessDeniedHandler;           // 403 형식

    // 인증 없이 접근 허용할 경로 (화이트리스트)
    private static final String[] WHITELIST = {
            "/api/auth/signup",
            "/api/auth/login",
            "/api/auth/reissue",
            "/api/auth/logout",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/h2-console/**",
            "/swagger-ui/**",
            "/swagger-ui.html"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 프론트(Vite) 개발 서버 오리진 허용 — CorsConfig의 CorsConfigurationSource 빈을 사용
            .cors(cors -> {})
            // JWT는 브라우저 쿠키가 아니라 헤더로 오므로 CSRF 불필요
            .csrf(csrf -> csrf.disable())
            // H2 콘솔이 프레임을 사용하므로 로컬 개발 편의를 위해 허용
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            // 세션을 만들지 않는 무상태 방식
            .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(WHITELIST).permitAll()  // 회원가입/로그인/Swagger/H2 콘솔은 공개
                    .anyRequest().authenticated()            // 나머지는 전부 인증 필요
            )
            // ★ 시큐리티가 던지는 인증/인가 예외도 공통 응답 형식으로 내려준다
            .exceptionHandling(ex -> ex
                    .authenticationEntryPoint(authenticationEntryPoint) // 401 UNAUTHORIZED
                    .accessDeniedHandler(accessDeniedHandler)           // 403 FORBIDDEN
            )
            // 우리가 만든 JWT 필터를 기본 인증 필터 앞에 끼운다
            .addFilterBefore(new JwtAuthenticationFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();   // 비밀번호는 BCrypt로만 저장
    }
}
