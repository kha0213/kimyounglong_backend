package com.yl.wirebarley.account.service;

import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.account.exception.AccountException;
import com.yl.wirebarley.account.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;

    public void save(AccountCreateRequest request) {
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw AccountException.accountDuplicated(request.getAccountNumber());
        }
        accountRepository.save(Account.create(request));
    }

    public void delete(Long id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> AccountException.accountNotFound(id));
        accountRepository.delete(account);
    }
}
