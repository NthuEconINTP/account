package com.moneyflow.account.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice(basePackages = "com.moneyflow.account",
annotations = org.springframework.web.bind.annotation.RestController.class)
// 這是一個全域失敗的統一，會去無腦接SERVICE的錯誤message，可看 AuthController
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleSystem(Exception ex, HttpServletRequest request) throws Exception {
        if (request.getRequestURI().startsWith("/v3/api-docs") || request.getRequestURI().startsWith("/swagger-ui")) {
            throw ex; // Swagger 自己處理
        }
        // 如果是你的業務例外訊息，直接回傳
        if (ex.getMessage() != null) {
            return ResponseEntity.badRequest().body(ApiResponseUtil.error(ex.getMessage()));
        }
        return ResponseEntity.status(500)
                             .body(ApiResponseUtil.error("Internal Server Error"));
    }
}