package com.yl.wirebarley.transaction.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WithdrawalRequest {
    private Long accountId;
    private BigDecimal amount;
}
