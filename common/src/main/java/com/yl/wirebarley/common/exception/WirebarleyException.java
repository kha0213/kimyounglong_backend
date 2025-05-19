package com.yl.wirebarley.common.exception;

import lombok.Getter;

@Getter
public class WirebarleyException extends RuntimeException {
    private final ErrorCode code;
    private final String message;

    private WirebarleyException(ErrorCode errorCode, Object... args) {
        super(errorCode.getMessage(args));
        this.code = errorCode;
        this.message = errorCode.getMessage(args);
    }

    public static WirebarleyException accountNotFound(Long id) {
        return new WirebarleyException(ErrorCode.ACCOUNT_NOT_FOUND, id);
    }
}
