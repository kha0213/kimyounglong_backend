package com.yl.wirebarley.account.helper;

import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.domain.Bank;
import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;

import java.math.BigDecimal;

import static org.springframework.test.util.ReflectionTestUtils.setField;

public class TestHelper {
    // 모든 Entity는 Long인 pk값을 가지고 있어서 테스트를 위해 id set하는 함수
    public static <T> void setId(T entity, Long id) {
        setField(entity, "id", id);
    }

    public static Account getAccount(Long id, Bank bank, String accountNumber, String accountHolder, BigDecimal balance) {
        AccountCreateRequest request = new AccountCreateRequest();
        request.setAccountNumber(accountNumber);
        request.setAccountHolder(accountHolder);
        request.setBank(bank);
        Account account = Account.create(request);
        setField(account, "id", id);
        setField(account, "balance", balance);

        return account;
    }
}
