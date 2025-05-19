package com.yl.wirebarley.common.exception;

import lombok.Getter;

@Getter
public class WirebarleyException extends RuntimeException {
    private final ErrorCode code;
    private final String message;

    protected WirebarleyException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.code = errorCode;
        this.message = errorCode.getMessage(args);
    }

    // 공통 예외 생성 메서드
    public static WirebarleyException of(ErrorCode errorCode, Object... args) {
        return new WirebarleyException(errorCode, args);
    }
}
