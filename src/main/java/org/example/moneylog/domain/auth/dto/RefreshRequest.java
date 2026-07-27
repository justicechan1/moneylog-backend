package org.example.moneylog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "토큰 재발급/로그아웃 요청")
public record RefreshRequest(
        @Schema(description = "로그인 시 발급받은 refreshToken")
        @NotBlank String refreshToken) {
}
