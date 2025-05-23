package com.yl.wirebarley.transaction.domain;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 거래 상태
 * <ul>
 * <li>PENDING - 처리 중</li>
 * <li>COMPLETED - 완료됨</li>
 * <li>FAILED - 실패</li>
 * </ul>
 */
@Schema(description = "거래 상태 (PENDING:처리중, COMPLETED:완료, FAILED:실패)",
        allowableValues = {"PENDING", "COMPLETED", "FAILED"})
public enum TransactionStatus {
    /** 처리 중 */
    PENDING,
    /** 완료됨 */
    COMPLETED,
    /** 실패 */
    FAILED
}
