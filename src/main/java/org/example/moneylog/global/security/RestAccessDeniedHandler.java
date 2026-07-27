package org.example.moneylog.global.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.moneylog.common.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

// 403 — 인증은 됐지만 권한 없음(시큐리티 단계)
@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException) throws IOException {
        RestAuthenticationEntryPoint.write(response, HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN);
    }
}
