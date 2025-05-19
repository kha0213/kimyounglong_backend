package com.yl.wirebarley.common.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice(basePackages = {
    "com.yl.wirebarley.common",
    "com.yl.wirebarley.account"
})
public class GlobalExceptionHandler {

    @ExceptionHandler(WirebarleyException.class)
    public ResponseEntity<String> handleCustomException(WirebarleyException e) {
        log.error("WirebarleyException occurred: {}", e.getMessage(), e);

        return ResponseEntity.status(getHttpStatusFromErrorCode(e.getCode()))
                .body(e.getMessage());
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class, InvalidFormatException.class,
            MethodArgumentTypeMismatchException.class})
    public ResponseEntity<String> handleValidationException(Exception e) {
        log.error("Validation error occurred: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorCode.BAD_REQUEST_BODY.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleUnexpectedException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorCode.INTERNAL_SERVER_ERROR.getMessage());
    }

    private HttpStatus getHttpStatusFromErrorCode(ErrorCode code) {
        for (ErrorCode errorCode : ErrorCode.values()) {
            if (errorCode.equals(code)) {
                return errorCode.getHttpStatus();
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
