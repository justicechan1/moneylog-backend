package org.example.moneylog.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.moneylog.domain.user.entitiy.User;

@Schema(description = "회원가입 응답")
public record SignupResponse(
        @Schema(description = "회원 ID", example = "1") Long userId,
        @Schema(description = "이메일", example = "user@example.com") String email,
        @Schema(description = "닉네임", example = "머니로거") String nickname) {
    public static SignupResponse from(User user) {
        return new SignupResponse(user.getId(), user.getEmail(), user.getNickname());
    }
}
