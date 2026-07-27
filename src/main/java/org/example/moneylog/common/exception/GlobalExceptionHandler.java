package org.example.moneylog.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.example.moneylog.common.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ① 우리가 던진 도메인 예외 → ErrorCode의 상태/코드/메시지로 형식 생성
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustom(CustomException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.error(code.name(), code.getMessage()));
    }

    // ② @Valid 검증 실패 → 400 VALIDATION_ERROR (첫 번째 필드 메시지를 안내)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidation(MethodArgumentNotValidException e) {
        String detail = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .orElse(ErrorCode.VALIDATION_ERROR.getMessage());
        return ResponseEntity
                .status(ErrorCode.VALIDATION_ERROR.getStatus())
                .body(ApiResponse.error(ErrorCode.VALIDATION_ERROR.name(), detail));
    }

    // ③ 스프링 시큐리티 인가 예외(@PreAuthorize 등에서 발생) → 403 FORBIDDEN
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException e) {
        ErrorCode code = ErrorCode.FORBIDDEN;
        return ResponseEntity
                .status(code.getStatus())
                .body(ApiResponse.error(code.name(), code.getMessage()));
    }

    // ④ 최후의 방어선: 예상 못 한 예외는 500으로, 상세는 로그로만 (민감정보 노출 금지)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnknown(Exception e) {
        log.error("Unhandled exception", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."));
    }
}
