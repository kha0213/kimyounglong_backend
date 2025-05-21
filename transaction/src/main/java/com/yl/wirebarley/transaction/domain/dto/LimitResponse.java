package com.yl.wirebarley.transaction.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 계좌의 남은 일별 한도 정보를 담는 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LimitResponse {
    
    /**
     * 계좌 ID
     */
    private Long accountId;
    
    /**
     * 남은 일별 출금 한도
     */
    private BigDecimal remainingWithdrawalLimit;
    
    /**
     * 남은 일별 이체 한도
     */
    private BigDecimal remainingTransferLimit;
}