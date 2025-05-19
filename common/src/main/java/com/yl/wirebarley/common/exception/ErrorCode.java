package com.yl.wirebarley.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 BAD_REQUEST
    BAD_REQUEST_BODY(HttpStatus.BAD_REQUEST, "유효하지 않은 값입니다."),

    // 404 NOT_FOUND
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "계좌정보를 찾을 수 없습니다. accountId :[%d]"),

    // 500 INTERNAL SERVER ERROR
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus httpStatus;
    private final String messageTemplate;

    public String getMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}
