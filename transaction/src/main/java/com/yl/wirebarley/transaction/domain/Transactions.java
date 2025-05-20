package com.yl.wirebarley.transaction.domain;

import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor
public class Transactions extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, referencedColumnName = "id")
    private Account account;

    @ManyToOne
    @JoinColumn(name = "target_account_id", referencedColumnName = "id")
    private Account targetAccount;

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
}
