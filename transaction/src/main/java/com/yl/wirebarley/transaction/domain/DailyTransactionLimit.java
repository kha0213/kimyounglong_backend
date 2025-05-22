package com.yl.wirebarley.transaction.domain;

import com.yl.wirebarley.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 계좌별 일일 거래 한도를 관리하는 엔티티
 */
@Entity
@Table(name = "daily_transaction_limits", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "type", "date"}))
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DailyTransactionLimit extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "account_id", nullable = false)
    private Long accountId;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false)
    private BigDecimal totalAmount = BigDecimal.ZERO;
    
    @Version
    private Long version;
    
    public BigDecimal addAmount(BigDecimal amount) {
        this.totalAmount = this.totalAmount.add(amount);
        return this.totalAmount;
    }
    
    public static DailyTransactionLimit create(Long accountId, TransactionType type, LocalDate date) {
        DailyTransactionLimit limit = new DailyTransactionLimit();
        limit.accountId = accountId;
        limit.type = type;
        limit.date = date;
        limit.totalAmount = BigDecimal.ZERO;
        return limit;
    }
}