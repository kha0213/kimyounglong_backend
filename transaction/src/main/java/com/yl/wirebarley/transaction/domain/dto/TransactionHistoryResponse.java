package com.yl.wirebarley.transaction.domain.dto;

import com.yl.wirebarley.transaction.domain.TransactionStatus;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.domain.Transactions;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@Schema(description = "거래 내역 응답")
public class TransactionHistoryResponse {
    
    @Schema(description = "거래 ID", example = "1")
    private Long transactionId;
    
    @Schema(description = "거래 유형", example = "TRANSFER")
    private TransactionType type;
    
    @Schema(description = "거래 금액", example = "10000")
    private BigDecimal amount;
    
    @Schema(description = "수수료", example = "100")
    private BigDecimal fee;
    
    @Schema(description = "거래 설명/메모", example = "친구에게 송금")
    private String description;
    
    @Schema(description = "상대방 계좌 ID", example = "2")
    private Long counterpartyAccountId;
    
    @Schema(description = "거래 상태", example = "COMPLETED")
    private TransactionStatus status;
    
    @Schema(description = "거래 시간", example = "2024-12-27T10:30:00")
    private LocalDateTime transactionTime;
    
    @Schema(description = "잔액 변동", example = "-10100")
    private BigDecimal balanceChange;
    
    public static TransactionHistoryResponse from(Transactions transaction) {
        BigDecimal balanceChange = calculateBalanceChange(transaction);
        
        return TransactionHistoryResponse.builder()
                .transactionId(transaction.getId())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .fee(transaction.getFee())
                .description(transaction.getDescription())
                .counterpartyAccountId(getCounterpartyAccountId(transaction))
                .status(transaction.getStatus())
                .transactionTime(transaction.getTransactionTime())
                .balanceChange(balanceChange)
                .build();
    }
    
    private static BigDecimal calculateBalanceChange(Transactions transaction) {
        switch (transaction.getType()) {
            case DEPOSIT:
            case TRANSFER_RECEIVE:
                return transaction.getAmount();
            case WITHDRAWAL:
                return transaction.getAmount().negate();
            case TRANSFER:
                // 송금의 경우 금액 + 수수료가 차감됨
                return transaction.getAmount().add(transaction.getFee()).negate();
            default:
                return BigDecimal.ZERO;
        }
    }
    
    private static Long getCounterpartyAccountId(Transactions transaction) {
        if (transaction.getType() == TransactionType.TRANSFER || 
            transaction.getType() == TransactionType.TRANSFER_RECEIVE) {
            return transaction.getTargetAccountId();
        }
        return null;
    }
}
