package com.yl.wirebarley.transaction.exception;

import com.yl.wirebarley.common.exception.ErrorCode;
import com.yl.wirebarley.common.exception.WirebarleyException;

public class TransactionException extends WirebarleyException {
    
    protected TransactionException(ErrorCode errorCode, Object... args) {
        super(errorCode, args);
    }
    
    public static TransactionException insufficientBalance() {
        return new TransactionException(ErrorCode.INSUFFICIENT_BALANCE);
    }
    
    public static TransactionException dailyWithdrawalLimitExceeded() {
        return new TransactionException(ErrorCode.DAILY_WITHDRAWAL_LIMIT_EXCEEDED);
    }

    public static TransactionException dailyTransferLimitExceeded() {
        return new TransactionException(ErrorCode.DAILY_TRANSFER_LIMIT_EXCEEDED);
    }
}
