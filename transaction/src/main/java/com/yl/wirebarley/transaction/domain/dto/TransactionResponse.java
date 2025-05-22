package com.yl.wirebarley.transaction.domain.dto;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.transaction.domain.TransactionStatus;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.domain.Transactions;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "거래 처리 결과 정보")
public class TransactionResponse {
    
    @Schema(description = "거래 ID", example = "1")
    private Long transactionId;
    
    @Schema(description = "계좌 ID", example = "1")
    private Long accountId;
    
    @Schema(description = "대상 계좌 ID (이체시에만 사용)", example = "2")
    private Long targetAccountId;

    @Schema(description = "거래 금액", example = "10000")
    private BigDecimal amount;
    
    @Schema(description = "수수료", example = "1000")
    private BigDecimal fee;
    
    @Schema(description = "거래 후 잔액", example = "50000")
    private BigDecimal balance;

    @Schema(description = "거래 유형", example = "DEPOSIT")
    private TransactionType type;
    
    @Schema(description = "거래 설명", example = "입금")
    private String description;

    @Schema(description = "거래 시간", example = "2023-12-01T10:30:00")
    private LocalDateTime transactionTime;
    
    @Schema(description = "거래 상태", example = "COMPLETED")
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