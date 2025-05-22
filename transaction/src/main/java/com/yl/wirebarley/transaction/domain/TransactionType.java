package com.yl.wirebarley.transaction.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 거래 유형
 * <ul>
 * <li>DEPOSIT - 입금</li>
 * <li>WITHDRAWAL - 출금</li>
 * <li>TRANSFER - 이체 (송금자 측)</li>
 * <li>TRANSFER_RECEIVE - 이체 (수취인 측)</li>
 * </ul>
 */
@Schema(description = "거래 유형 (DEPOSIT:입금, WITHDRAWAL:출금, TRANSFER:이체송금, TRANSFER_RECEIVE:이체수취)",
        allowableValues = {"DEPOSIT", "WITHDRAWAL", "TRANSFER", "TRANSFER_RECEIVE"})
public enum TransactionType {
    /** 입금 */
    DEPOSIT,
    /** 출금 */
    WITHDRAWAL,
    /** 이체 (송금자 측) */
    TRANSFER,
    /** 이체 (수취인 측) */
    TRANSFER_RECEIVE
}
