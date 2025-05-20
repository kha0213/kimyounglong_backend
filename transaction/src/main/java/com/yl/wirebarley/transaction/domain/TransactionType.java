package com.yl.wirebarley.transaction.domain;

public enum TransactionType {
    DEPOSIT,       // 입금
    WITHDRAWAL,    // 출금
    TRANSFER,      // 이체 (송금자 측)
    TRANSFER_RECEIVE // 이체 (수취인 측)
}
