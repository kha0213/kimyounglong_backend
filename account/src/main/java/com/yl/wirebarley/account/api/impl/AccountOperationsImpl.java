package com.yl.wirebarley.account.api.impl;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.account.api.AccountOperations;
import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.exception.AccountException;
import com.yl.wirebarley.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AccountOperationsImpl implements AccountOperations {
    private final AccountRepository accountRepository;

    @Override
    public Optional<AccountDto> getAccount(Long accountId) {
        Optional<Account> account = accountRepository.findById(accountId);
        return account.map(AccountDto::of);
    }

    @Override
    @Transactional
    public AccountDto updateBalance(Long accountId, BigDecimal amount) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> AccountException.accountNotFound(accountId));
        
        // 출금인 경우 잔액 확인
        if (amount.compareTo(BigDecimal.ZERO) < 0 && 
                account.getBalance().compareTo(amount.abs()) < 0) {
            throw AccountException.insufficientBalance(accountId);
        }
        
        account.addBalance(amount);
        Account updatedAccount = accountRepository.save(account);

        return AccountDto.of(updatedAccount);
    }
}
