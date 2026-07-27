package org.example.moneylog.domain.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.moneylog.common.response.ApiResponse;
import org.example.moneylog.domain.auth.dto.LoginRequest;
import org.example.moneylog.domain.auth.dto.RefreshRequest;
import org.example.moneylog.domain.auth.dto.SignupRequest;
import org.example.moneylog.domain.auth.dto.SignupResponse;
import org.example.moneylog.domain.auth.dto.TokenResponse;
import org.example.moneylog.domain.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "인증", description = "회원가입 및 로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "회원가입", description = "이메일, 비밀번호, 닉네임으로 신규 회원을 등록합니다.")
    @SecurityRequirements
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest req) {
        SignupResponse data = authService.signup(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입에 성공했습니다.", data));
    }

    @Operation(summary = "로그인", description = "이메일/비밀번호로 인증 후 JWT accessToken/refreshToken을 발급합니다.")
    @SecurityRequirements
    @PostMapping("/login")
    public ApiResponse<TokenResponse> login(@Valid @RequestBody LoginRequest req) {
        TokenResponse data = authService.login(req);
        return ApiResponse.success("로그인에 성공했습니다.", data);
    }

    @Operation(summary = "토큰 재발급", description = "refreshToken을 검증한 뒤 accessToken/refreshToken을 새로 발급합니다(회전 방식, 기존 refreshToken은 폐기됩니다).")
    @SecurityRequirements
    @PostMapping("/reissue")
    public ApiResponse<TokenResponse> reissue(@Valid @RequestBody RefreshRequest req) {
        TokenResponse data = authService.reissue(req);
        return ApiResponse.success("토큰 재발급에 성공했습니다.", data);
    }

    @Operation(summary = "로그아웃", description = "서버에 저장된 refreshToken을 폐기합니다. 이후 해당 refreshToken으로는 재발급이 불가능합니다.")
    @SecurityRequirements
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody RefreshRequest req) {
        authService.logout(req);
        return ApiResponse.success("로그아웃에 성공했습니다.", null);
    }
}
