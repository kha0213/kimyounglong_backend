package com.yl.wirebarley.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common Exception
    BAD_REQUEST_BODY(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // Account Exception
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌정보를 찾을 수 없습니다. accountId: [%d]"),
    ACCOUNT_DUPLICATED(HttpStatus.CONFLICT, "이미 등록된 계좌정보 입니다. 계좌번호: [%s]"),

    // Transaction Exception
    INSUFFICIENT_BALANCE(HttpStatus.PAYMENT_REQUIRED, "잔액이 부족합니다."),
    DAILY_WITHDRAWAL_LIMIT_EXCEEDED(HttpStatus.PAYMENT_REQUIRED, "출금 일일 한도를 초과했습니다."),
    DAILY_TRANSFER_LIMIT_EXCEEDED(HttpStatus.PAYMENT_REQUIRED, "이체 일일 한도를 초과했습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;

    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
