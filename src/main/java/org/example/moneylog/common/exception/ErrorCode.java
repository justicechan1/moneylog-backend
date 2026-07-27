package org.example.moneylog.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 — 검증 실패
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    // 401 — 로그인 자격증명 불일치
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "이메일 또는 비밀번호가 올바르지 않습니다."),
    // 401 — 토큰 없음/위조/만료
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    // 401 — refreshToken 위조/만료/폐기(회전됨)
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 refreshToken입니다. 다시 로그인해 주세요."),
    // 403 — 인가 실패(본인 리소스 아님)
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    // 409 — 이메일 중복
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "이미 사용 중인 이메일입니다."),
    // 404 — 리소스 없음
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "존재하지 않는 카테고리입니다."),
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래내역을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;
}
