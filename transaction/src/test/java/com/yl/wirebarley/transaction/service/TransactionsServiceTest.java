package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.account.api.AccountOperations;
import com.yl.wirebarley.transaction.domain.TransactionStatus;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.domain.Transactions;
import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.TransactionResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.repository.TransactionsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static com.yl.wirebarley.transaction.helper.TestHelper.setId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionsServiceTest {

    @Mock
    private TransactionsRepository transactionsRepository;

    @Mock
    private AccountOperations accountOperations;

    @Mock
    private TransactionLimitService limitService;

    @InjectMocks
    private TransactionsService transactionsService;

    @BeforeEach
    void setUp() {
        // 프로퍼티 값 설정
        ReflectionTestUtils.setField(transactionsService, "DAILY_WITHDRAWAL_LIMIT", new BigDecimal("1000000"));
        ReflectionTestUtils.setField(transactionsService, "DAILY_TRANSFER_LIMIT", new BigDecimal("3000000"));
        ReflectionTestUtils.setField(transactionsService, "TRANSFER_FEE", new BigDecimal("0.01"));
    }

    @Test
    @DisplayName("입금 요청 성공 테스트")
    void depositSuccessTest() {
        // Given
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("10000");
        DepositRequest request = new DepositRequest();
        request.setAccountId(accountId);
        request.setAmount(amount);

        AccountDto accountDto = AccountDto.builder()
            .id(accountId)
            .accountNumber("1234567890")
            .accountHolder("테스트 계좌")
            .balance(new BigDecimal("50000"))
            .build();
            
        AccountDto updatedAccountDto = AccountDto.builder()
            .id(accountId)
            .accountNumber("1234567890")
            .accountHolder("테스트 계좌")
            .balance(new BigDecimal("60000"))
            .build();
        
        Transactions savedTransaction = Transactions.createForDeposit(request);
        setId(savedTransaction, 1L);
        savedTransaction.markAsCompleted();

        when(accountOperations.getAccount(accountId)).thenReturn(Optional.of(accountDto));
        when(accountOperations.updateBalance(accountId, amount)).thenReturn(updatedAccountDto);
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(savedTransaction);

        // When
        TransactionResponse response = transactionsService.deposit(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTransactionId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getType()).isEqualTo(TransactionType.DEPOSIT);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.getBalance()).isEqualTo(new BigDecimal("60000"));
        
        verify(accountOperations).getAccount(accountId);
        verify(accountOperations).updateBalance(accountId, amount);
        verify(transactionsRepository).save(any(Transactions.class));
    }

    @Test
    @DisplayName("출금 요청 성공 테스트")
    void withdrawalSuccessTest() {
        // Given
        Long accountId = 1L;
        BigDecimal amount = new BigDecimal("10000");
        WithdrawalRequest request = new WithdrawalRequest();
        request.setAccountId(accountId);
        request.setAmount(amount);

        AccountDto accountDto = AccountDto.builder()
            .id(accountId)
            .accountNumber("1234567890")
            .accountHolder("테스트 계좌")
            .balance(new BigDecimal("50000"))
            .build();
            
        AccountDto updatedAccountDto = AccountDto.builder()
            .id(accountId)
            .accountNumber("1234567890")
            .accountHolder("테스트 계좌")
            .balance(new BigDecimal("40000"))
            .build();
        
        Transactions savedTransaction = Transactions.createForWithdrawal(request);
        setId(savedTransaction, 1L);
        savedTransaction.markAsCompleted();

        when(accountOperations.getAccount(accountId)).thenReturn(Optional.of(accountDto));
        when(accountOperations.updateBalance(accountId, amount.negate())).thenReturn(updatedAccountDto);
        when(transactionsRepository.save(any(Transactions.class))).thenReturn(savedTransaction);
        when(limitService.checkAndUpdateLimit(anyLong(), any(TransactionType.class), any(BigDecimal.class), any(BigDecimal.class))).thenReturn(new BigDecimal("10000"));

        // When
        TransactionResponse response = transactionsService.withdrawal(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTransactionId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getType()).isEqualTo(TransactionType.WITHDRAWAL);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.getBalance()).isEqualTo(new BigDecimal("40000"));
        
        verify(accountOperations).getAccount(accountId);
        verify(accountOperations).updateBalance(accountId, amount.negate());
        verify(limitService).checkAndUpdateLimit(eq(accountId), eq(TransactionType.WITHDRAWAL), eq(amount), any(BigDecimal.class));
        verify(transactionsRepository).save(any(Transactions.class));
    }

    @Test
    @DisplayName("계좌 이체 성공 테스트")
    void transferSuccessTest() {
        // Given
        Long sourceAccountId = 1L;
        Long targetAccountId = 2L;
        BigDecimal amount = new BigDecimal("10000");
        BigDecimal fee = amount.multiply(new BigDecimal("0.01")); // 100원
        BigDecimal totalAmount = amount.add(fee); // 10100원
        
        TransferRequest request = new TransferRequest();
        request.setAccountId(sourceAccountId);
        request.setTargetAccountId(targetAccountId);
        request.setAmount(amount);

        AccountDto sourceAccountDto = AccountDto.builder()
            .id(sourceAccountId)
            .accountNumber("1234567890")
            .accountHolder("출금 계좌")
            .balance(new BigDecimal("50000"))
            .build();
            
        AccountDto targetAccountDto = AccountDto.builder()
            .id(targetAccountId)
            .accountNumber("0987654321")
            .accountHolder("입금 계좌")
            .balance(new BigDecimal("30000"))
            .build();
            
        AccountDto updatedSourceAccountDto = AccountDto.builder()
            .id(sourceAccountId)
            .accountNumber("1234567890")
            .accountHolder("출금 계좌")
            .balance(new BigDecimal("39900"))
            .build();
            
        AccountDto updatedTargetAccountDto = AccountDto.builder()
            .id(targetAccountId)
            .accountNumber("0987654321")
            .accountHolder("입금 계좌")
            .balance(new BigDecimal("40000"))
            .build();
        
        Transactions sourceTransaction = Transactions.createForTransfer(request, fee);
        setId(sourceTransaction, 1L);
        sourceTransaction.markAsCompleted();

        Transactions targetTransaction = Transactions.createForTransferReceiver(request);
        setId(targetTransaction, 2L);
        targetTransaction.markAsCompleted();

        when(accountOperations.getAccount(sourceAccountId)).thenReturn(Optional.of(sourceAccountDto));
        when(accountOperations.getAccount(targetAccountId)).thenReturn(Optional.of(targetAccountDto));
        when(accountOperations.updateBalance(sourceAccountId, totalAmount.negate())).thenReturn(updatedSourceAccountDto);
        when(accountOperations.updateBalance(targetAccountId, amount)).thenReturn(updatedTargetAccountDto);
        when(transactionsRepository.save(any(Transactions.class)))
                .thenReturn(sourceTransaction)
                .thenReturn(targetTransaction);
        when(limitService.checkAndUpdateLimit(anyLong(), any(TransactionType.class), any(BigDecimal.class), any(BigDecimal.class))).thenReturn(new BigDecimal("10000"));

        // When
        TransactionResponse response = transactionsService.transfer(request);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getTransactionId()).isEqualTo(1L);
        assertThat(response.getAmount()).isEqualTo(amount);
        assertThat(response.getFee()).isEqualTo(fee);
        assertThat(response.getType()).isEqualTo(TransactionType.TRANSFER);
        assertThat(response.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
        assertThat(response.getBalance()).isEqualTo(new BigDecimal("39900"));
        
        verify(accountOperations).getAccount(sourceAccountId);
        verify(accountOperations).getAccount(targetAccountId);
        verify(accountOperations).updateBalance(sourceAccountId, totalAmount.negate());
        verify(accountOperations).updateBalance(targetAccountId, amount);
        verify(limitService).checkAndUpdateLimit(eq(sourceAccountId), eq(TransactionType.TRANSFER), eq(amount), any(BigDecimal.class));
        verify(transactionsRepository, times(2)).save(any(Transactions.class));
    }

    @Test
    @DisplayName("일일 출금 한도 조회 테스트")
    void getRemainingWithdrawalLimitTest() {
        // Given
        Long accountId = 1L;
        BigDecimal expectedLimit = new BigDecimal("800000");
        
        when(limitService.getRemainingLimit(eq(accountId), eq(TransactionType.WITHDRAWAL), any(BigDecimal.class)))
                .thenReturn(expectedLimit);

        // When
        BigDecimal result = transactionsService.getRemainingWithdrawalLimit(accountId);

        // Then
        assertThat(result).isEqualTo(expectedLimit);
        verify(limitService).getRemainingLimit(eq(accountId), eq(TransactionType.WITHDRAWAL), any(BigDecimal.class));
    }

    @Test
    @DisplayName("일일 이체 한도 조회 테스트")
    void getRemainingTransferLimitTest() {
        // Given
        Long accountId = 1L;
        BigDecimal expectedLimit = new BigDecimal("2500000");
        
        when(limitService.getRemainingLimit(eq(accountId), eq(TransactionType.TRANSFER), any(BigDecimal.class)))
                .thenReturn(expectedLimit);

        // When
        BigDecimal result = transactionsService.getRemainingTransferLimit(accountId);

        // Then
        assertThat(result).isEqualTo(expectedLimit);
        verify(limitService).getRemainingLimit(eq(accountId), eq(TransactionType.TRANSFER), any(BigDecimal.class));
    }
}