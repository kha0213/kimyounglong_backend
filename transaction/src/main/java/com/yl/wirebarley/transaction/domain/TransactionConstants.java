package com.yl.wirebarley.transaction.domain;

import java.math.BigDecimal;

public class TransactionConstants {
    // 수수료율 (1%)
    public static final BigDecimal TRANSFER_FEE_RATE = new BigDecimal("0.01");
    
    // 출금 일일 한도
    public static final BigDecimal WITHDRAWAL_DAILY_LIMIT = new BigDecimal("1000000");
    
    // 이체 일일 한도
    public static final BigDecimal TRANSFER_DAILY_LIMIT = new BigDecimal("3000000");
    
    // 최소 거래 금액
    public static final BigDecimal MINIMUM_TRANSACTION_AMOUNT = new BigDecimal("0.01");
}
