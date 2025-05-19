package com.yl.wirebarley.account.domain;

import com.yl.wirebarley.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "account_histories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountHistory extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Enumerated(EnumType.STRING)
    private Bank bank;

    @Column
    private String accountNumber;

    @Column
    private String accountHolder;

    @Column
    private BigDecimal balance;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    private Account account;

    public static AccountHistory of(Account account) {
        AccountHistory entity = new AccountHistory();
        entity.account = account;
        entity.bank = account.getBank();
        entity.accountNumber = account.getAccountNumber();
        entity.balance = account.getBalance();
        return entity;
    }
}