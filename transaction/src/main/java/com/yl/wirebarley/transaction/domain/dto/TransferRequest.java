package com.yl.wirebarley.transaction.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferRequest {
    private Long accountId;
    private Long targetAccountId;
    private BigDecimal amount;
    private String receiverMemo;
    private String senderMemo;
}
