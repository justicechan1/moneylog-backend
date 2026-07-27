package org.example.moneylog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인/토큰 재발급 응답")
public record TokenResponse(
        @Schema(description = "JWT accessToken") String accessToken,
        @Schema(description = "JWT refreshToken") String refreshToken,
        @Schema(description = "토큰 타입", example = "Bearer") String tokenType,
        @Schema(description = "닉네임", example = "머니로거") String nickname) {
    public static TokenResponse of(String accessToken, String refreshToken, String nickname) {
        return new TokenResponse(accessToken, refreshToken, "Bearer", nickname);
    }
}
