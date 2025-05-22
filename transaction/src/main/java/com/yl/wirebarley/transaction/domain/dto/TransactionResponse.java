package com.yl.wirebarley.transaction.domain.dto;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.transaction.domain.TransactionStatus;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.domain.Transactions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private Long accountId;
    private Long targetAccountId;

    private BigDecimal amount;
    private BigDecimal fee;
    private BigDecimal balance;

    private TransactionType type;
    private String description;

    private LocalDateTime transactionTime;
    private TransactionStatus status;

    public static TransactionResponse getResponse(Transactions transaction, AccountDto account) {
        return TransactionResponse.builder()
                .transactionId(transaction.getId())
                .accountId(account.getId())
                .targetAccountId(transaction.getTargetAccountId())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .balance(account.getBalance())
                .type(transaction.getType())
                .description(transaction.getDescription())
                .transactionTime(transaction.getTransactionTime())
                .status(transaction.getStatus())
                .build();
    }
}