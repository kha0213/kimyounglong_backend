package com.yl.wirebarley.account.service;

import com.yl.wirebarley.account.domain.Account;
import com.yl.wirebarley.account.domain.Bank;
import com.yl.wirebarley.account.domain.dto.AccountCreateRequest;
import com.yl.wirebarley.account.repository.AccountRepository;
import com.yl.wirebarley.common.exception.ErrorCode;
import com.yl.wirebarley.common.exception.WirebarleyException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    // 테스트 객체
    private AccountCreateRequest accountCreateRequest;
    private Account account;

    @BeforeEach
    void setUp() {
        accountCreateRequest = new AccountCreateRequest();
        accountCreateRequest.setAccountNumber("1234-5678-90");
        accountCreateRequest.setAccountHolder("A");
        accountCreateRequest.setBank(Bank.KB);

        account = Account.create(accountCreateRequest);
        TestHelper.setId(account, 1L);
    }

    @Test
    @DisplayName("계좌 생성 테스트")
    void save_ShouldCreateAccount() {
        // Given
        when(accountRepository.save(any(Account.class))).thenReturn(account);
        
        // When
        accountService.save(accountCreateRequest);
        
        // Then
        verify(accountRepository, times(1)).save(any(Account.class));
    }

    @Test
    @DisplayName("계좌 삭제 테스트 - 존재하는 계좌")
    void delete_ExistingAccount_ShouldDeleteAccount() {
        // Given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.of(account));
        
        // When
        accountService.delete(1L);
        
        // Then
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    @DisplayName("계좌 삭제 테스트 - 존재하지 않는 계좌")
    void delete_NonExistingAccount_ShouldThrowException() {
        // Given
        when(accountRepository.findById(anyLong())).thenReturn(Optional.empty());
        
        // When & Then
        WirebarleyException exception = assertThrows(WirebarleyException.class, () -> {
            accountService.delete(1L);
        });
        
        assertEquals(ErrorCode.ACCOUNT_NOT_FOUND, exception.getCode());
        
        verify(accountRepository, never()).delete(any(Account.class));
    }
}
