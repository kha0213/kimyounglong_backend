package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.account.api.AccountOperations;
import com.yl.wirebarley.account.domain.Bank;
import com.yl.wirebarley.transaction.domain.TransactionStatus;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.domain.Transactions;
import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.TransactionResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.exception.TransactionException;
import com.yl.wirebarley.transaction.repository.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private AccountOperations accountOperations;

    @InjectMocks
    private TransactionsService transactionsService;

    private AccountDto sourceAccount;
    private AccountDto targetAccount;
    private Transactions mockTransaction;

    @BeforeEach
    void setUp() {
        // Setup source account
        sourceAccount = AccountDto.builder()
                .id(1L)
                .accountNumber("1234567890")
                .accountHolder("A")
                .bank(Bank.KB)
                .balance(new BigDecimal("1000.00"))
                .build();

        // Setup target account
        targetAccount = AccountDto.builder()
                .id(2L)
                .accountNumber("0987654321")
                .accountHolder("B")
                .bank(Bank.SHINHAN)
                .balance(new BigDecimal("500.00"))
                .build();

        // Setup mock transaction
        mockTransaction = mock(Transactions.class);
        when(mockTransaction.getId()).thenReturn(1L);
        when(mockTransaction.getAccountId()).thenReturn(1L);
        when(mockTransaction.getTargetAccountId()).thenReturn(null);
        when(mockTransaction.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(mockTransaction.getFee()).thenReturn(BigDecimal.ZERO);
        when(mockTransaction.getType()).thenReturn(TransactionType.DEPOSIT);
        when(mockTransaction.getDescription()).thenReturn("Test transaction");
        when(mockTransaction.getStatus()).thenReturn(TransactionStatus.COMPLETED);
        when(mockTransaction.getTransactionTime()).thenReturn(LocalDateTime.now());
    }

    @Test
    void deposit_shouldSucceed() {
        // Given
        DepositRequest request = new DepositRequest(1L, new BigDecimal("100.00"), "Test deposit");
        when(accountOperations.existsAccount(1L)).thenReturn(true);
        when(accountOperations.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(sourceAccount);
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(mockTransaction);

        // When
        TransactionResponse response = transactionsService.deposit(request);

        // Then
        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals(1L, response.getAccountId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals("DEPOSIT", response.getType());
        verify(accountOperations).updateBalance(eq(1L), eq(new BigDecimal("100.00")));
        verify(transactionsRepository).save(any(Transactions.class));
    }

    @Test
    void withdrawal_shouldSucceed_whenSufficientBalance() {
        // Given
        WithdrawalRequest request = new WithdrawalRequest(1L, new BigDecimal("100.00"), "Test withdrawal");
        when(accountOperations.getAccount(1L)).thenReturn(sourceAccount);
        when(mockTransaction.getType()).thenReturn(TransactionType.WITHDRAWAL);
        when(accountOperations.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(sourceAccount);
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(mockTransaction);

        // When
        TransactionResponse response = transactionsService.withdrawal(request);

        // Then
        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals("WITHDRAWAL", response.getType());
        verify(accountOperations).updateBalance(eq(1L), eq(new BigDecimal("100.00").negate()));
        verify(transactionsRepository).save(any(Transactions.class));
    }

    @Test
    void withdrawal_shouldFail_whenInsufficientBalance() {
        // Given
        WithdrawalRequest request = new WithdrawalRequest(1L, new BigDecimal("2000.00"), "Test withdrawal");
        when(accountOperations.getAccount(1L)).thenReturn(sourceAccount);

        // When & Then
        assertThrows(TransactionException.class, () -> {
            transactionsService.withdrawal(request);
        });
        verify(accountOperations, never()).updateBalance(any(Long.class), any(BigDecimal.class));
    }

    @Test
    void transfer_shouldSucceed_whenSufficientBalance() {
        // Given
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("100.00"), "Test transfer", new BigDecimal("5.00"));
        when(accountOperations.getAccount(1L)).thenReturn(sourceAccount);
        when(accountOperations.getAccount(2L)).thenReturn(targetAccount);
        
        Transactions mockSourceTransaction = mock(Transactions.class);
        when(mockSourceTransaction.getId()).thenReturn(1L);
        when(mockSourceTransaction.getAccountId()).thenReturn(1L);
        when(mockSourceTransaction.getTargetAccountId()).thenReturn(2L);
        when(mockSourceTransaction.getAmount()).thenReturn(new BigDecimal("100.00"));
        when(mockSourceTransaction.getFee()).thenReturn(new BigDecimal("5.00"));
        when(mockSourceTransaction.getType()).thenReturn(TransactionType.TRANSFER);
        when(mockSourceTransaction.getDescription()).thenReturn("Test transfer");
        when(mockSourceTransaction.getStatus()).thenReturn(TransactionStatus.COMPLETED);
        when(mockSourceTransaction.getTransactionTime()).thenReturn(LocalDateTime.now());
        
        when(accountOperations.updateBalance(eq(1L), any(BigDecimal.class))).thenReturn(sourceAccount);
        when(accountOperations.updateBalance(eq(2L), any(BigDecimal.class))).thenReturn(targetAccount);
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(mockSourceTransaction);

        // When
        TransactionResponse response = transactionsService.transfer(request);

        // Then
        assertNotNull(response);
        assertEquals(TransactionStatus.COMPLETED, response.getStatus());
        assertEquals("TRANSFER", response.getType());
        assertEquals(1L, response.getAccountId());
        assertEquals(2L, response.getTargetAccountId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
        assertEquals(new BigDecimal("5.00"), response.getFee());
        
        // 송금자 계좌에서 금액+수수료 차감 확인
        verify(accountOperations).updateBalance(eq(1L), eq(new BigDecimal("105.00").negate()));
        // 수취인 계좌에 금액 추가 확인
        verify(accountOperations).updateBalance(eq(2L), eq(new BigDecimal("100.00")));
        // 두 개의 트랜잭션 저장 확인
        verify(transactionsRepository, times(2)).save(any(Transactions.class));
    }

    @Test
    void transfer_shouldFail_whenInsufficientBalance() {
        // Given
        TransferRequest request = new TransferRequest(1L, 2L, new BigDecimal("950.00"), "Test transfer", new BigDecimal("100.00"));
        when(accountOperations.getAccount(1L)).thenReturn(sourceAccount);
        when(accountOperations.getAccount(2L)).thenReturn(targetAccount);

        // When & Then
        assertThrows(TransactionException.class, () -> {
            transactionsService.transfer(request);
        });
        verify(accountOperations, never()).updateBalance(any(Long.class), any(BigDecimal.class));
        verify(transactionsRepository, never()).save(any(Transactions.class));
    }
}
