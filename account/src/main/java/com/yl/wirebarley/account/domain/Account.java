package com.yl.wirebarley.account.domain;

import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.common.entity.SoftDeleteBaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "accounts")
@Getter
@SQLRestriction("deleted = false")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account extends SoftDeleteBaseEntity {
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
    private BigDecimal balance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AccountHistory> histories = new ArrayList<>();

    public static Account create(AccountCreateRequest request) {
        Account account = new Account();
        account.accountNumber = request.getAccountNumber();
        account.accountHolder = request.getAccountHolder();
        account.bank = request.getBank();
        account.addHistory();
        return account;
    }

    public void addHistory() {
        AccountHistory history = AccountHistory.of(this);
        this.histories.add(history);
        history.setAccount(this);
    }
}