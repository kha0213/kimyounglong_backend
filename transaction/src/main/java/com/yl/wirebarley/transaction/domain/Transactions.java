package com.yl.wirebarley.transaction.domain;

import com.yl.wirebarley.common.entity.BaseEntity;
import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transactions extends BaseEntity {
    private static final String DEFAULT_DEPOSIT_DESCRIPTION = "입금";
    private static final String DEFAULT_WITHDRAWAL_DESCRIPTION = "출금";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long accountId;

    private Long targetAccountId;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column
    private BigDecimal fee = BigDecimal.ZERO;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private LocalDateTime transactionTime;
    
    @Column(nullable = false)
    private TransactionStatus status;

    // 트랜잭션 실패로 상태 변경
    public void markAsFailed() {
        this.status = TransactionStatus.FAILED;
    }

    public void markAsCompleted() {
        this.status = TransactionStatus.COMPLETED;
    }

    public static Transactions createForDeposit(DepositRequest request) {
        Transactions entity = new Transactions();
        entity.accountId = request.getAccountId();
        entity.type = TransactionType.DEPOSIT;
        entity.amount = request.getAmount();
        entity.description = DEFAULT_DEPOSIT_DESCRIPTION;
        entity.status = TransactionStatus.PENDING;
        entity.transactionTime = LocalDateTime.now();
        return entity;
    }

    public static Transactions createForWithdrawal(WithdrawalRequest request) {
        Transactions entity = new Transactions();
        entity.accountId = request.getAccountId();
        entity.type = TransactionType.WITHDRAWAL;
        entity.amount = request.getAmount();
        entity.description = DEFAULT_WITHDRAWAL_DESCRIPTION;
        entity.status = TransactionStatus.PENDING;
        entity.transactionTime = LocalDateTime.now();
        return entity;
    }

    public static Transactions createForTransfer(TransferRequest request, BigDecimal fee) {
        Transactions entity = new Transactions();
        entity.accountId = request.getAccountId();
        entity.targetAccountId = request.getTargetAccountId();
        entity.type = TransactionType.TRANSFER;
        entity.amount = request.getAmount();
        entity.fee = fee;
        entity.description = request.getSenderMemo();
        entity.status = TransactionStatus.PENDING;
        entity.transactionTime = LocalDateTime.now();
        return entity;
    }

    public static Transactions createForTransferReceiver(TransferRequest request) {
        Transactions entity = new Transactions();
        entity.accountId = request.getTargetAccountId();
        entity.targetAccountId = request.getAccountId();
        entity.type = TransactionType.TRANSFER_RECEIVE;
        entity.amount = request.getAmount();
        entity.description = request.getReceiverMemo();
        entity.status = TransactionStatus.PENDING;
        entity.transactionTime = LocalDateTime.now();
        return entity;
    }
}
