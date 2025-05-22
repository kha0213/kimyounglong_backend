package com.yl.wirebarley.account.api;

import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.domain.Bank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 계좌 정보를 담는 DTO 클래스
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDto {
    private Long id;
    private String accountNumber;
    private String accountHolder;
    private Bank bank;
    private BigDecimal balance;

    public static AccountDto of(Account entity) {
        return new AccountDto(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getAccountHolder(),
                entity.getBank(),
                entity.getBalance()
        );
    }
}
