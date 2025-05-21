package com.yl.wirebarley.account.exception;

import com.yl.wirebarley.common.exception.ErrorCode;
import com.yl.wirebarley.common.exception.WirebarleyException;

public class AccountException extends WirebarleyException {
    
    protected AccountException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public static AccountException accountNotFound(Long id) {
        return new AccountException(ErrorCode.ACCOUNT_NOT_FOUND, id);
    }
    
    public static AccountException accountDuplicated(String accountNumber) {
        return new AccountException(ErrorCode.ACCOUNT_DUPLICATED, accountNumber);
    }
    
    public static AccountException insufficientBalance(Long accountId) {
        return new AccountException(ErrorCode.INSUFFICIENT_BALANCE, accountId);
    }
}
