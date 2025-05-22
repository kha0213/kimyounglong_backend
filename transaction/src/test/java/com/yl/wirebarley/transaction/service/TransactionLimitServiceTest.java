package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.transaction.domain.DailyTransactionLimit;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.exception.TransactionException;
import com.yl.wirebarley.transaction.repository.DailyTransactionLimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionLimitServiceTest {

    @Mock
    private DailyTransactionLimitRepository limitRepository;

    @InjectMocks
    private TransactionLimitService limitService;

    private DailyTransactionLimit withdrawalLimit;
    private DailyTransactionLimit transferLimit;
    private LocalDate today;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        // 출금 한도 설정
        withdrawalLimit = DailyTransactionLimit.create(1L, TransactionType.WITHDRAWAL, today);
        withdrawalLimit.setTotalAmount(new BigDecimal("300000.00")); // 이미 30만원 출금

        // 이체 한도 설정
        transferLimit = DailyTransactionLimit.create(1L, TransactionType.TRANSFER, today);
        transferLimit.setTotalAmount(new BigDecimal("1000000.00")); // 이미 100만원 이체
    }

    @Test
    @DisplayName("처음 거래하는 계좌에 대한 한도 확인 및 업데이트 테스트")
    void checkAndUpdateLimit_FirstTransaction() {
        // Given
        when(limitRepository.findWithLockByAccountIdAndTypeAndDate(eq(2L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.empty());
        
        DailyTransactionLimit newLimit = DailyTransactionLimit.create(2L, TransactionType.WITHDRAWAL, today);
        newLimit.setTotalAmount(new BigDecimal("100000.00"));
        
        when(limitRepository.save(any(DailyTransactionLimit.class))).thenReturn(newLimit);

        // When
        BigDecimal result = limitService.checkAndUpdateLimit(
                2L, 
                TransactionType.WITHDRAWAL, 
                new BigDecimal("100000.00"), 
                new BigDecimal("1000000.00")
        );

        // Then
        assertEquals(new BigDecimal("100000.00"), result);
        verify(limitRepository).findWithLockByAccountIdAndTypeAndDate(eq(2L), eq(TransactionType.WITHDRAWAL), eq(today));
        verify(limitRepository).save(any(DailyTransactionLimit.class));
    }

    @Test
    @DisplayName("출금 한도 내에서 거래 성공 테스트")
    void checkAndUpdateLimit_WithdrawalWithinLimit() {
        // Given
        when(limitRepository.findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.of(withdrawalLimit));
        
        when(limitRepository.save(any(DailyTransactionLimit.class))).thenReturn(withdrawalLimit);

        // When
        BigDecimal result = limitService.checkAndUpdateLimit(
                1L, 
                TransactionType.WITHDRAWAL, 
                new BigDecimal("200000.00"), // 30만원(기존) + 20만원(새로운) = 50만원 < 100만원(한도)
                new BigDecimal("1000000.00")
        );

        // Then
        assertEquals(new BigDecimal("500000.00"), result); // 총 50만원
        verify(limitRepository).findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today));
        verify(limitRepository).save(any(DailyTransactionLimit.class));
    }

    @Test
    @DisplayName("출금 한도 초과 시 예외 발생 테스트")
    void checkAndUpdateLimit_WithdrawalExceedLimit() {
        // Given
        when(limitRepository.findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.of(withdrawalLimit));

        // When & Then
        assertThrows(TransactionException.class, () -> {
            limitService.checkAndUpdateLimit(
                    1L, 
                    TransactionType.WITHDRAWAL, 
                    new BigDecimal("800000.00"), // 30만원(기존) + 80만원(새로운) = 110만원 > 100만원(한도)
                    new BigDecimal("1000000.00")
            );
        });
        
        verify(limitRepository).findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today));
        verify(limitRepository, never()).save(any(DailyTransactionLimit.class));
    }

    @Test
    @DisplayName("이체 한도 내에서 거래 성공 테스트")
    void checkAndUpdateLimit_TransferWithinLimit() {
        // Given
        when(limitRepository.findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.TRANSFER), eq(today)))
                .thenReturn(Optional.of(transferLimit));
        
        when(limitRepository.save(any(DailyTransactionLimit.class))).thenReturn(transferLimit);

        // When
        BigDecimal result = limitService.checkAndUpdateLimit(
                1L, 
                TransactionType.TRANSFER, 
                new BigDecimal("1000000.00"), // 100만원(기존) + 100만원(새로운) = 200만원 < 300만원(한도)
                new BigDecimal("3000000.00")
        );

        // Then
        assertEquals(new BigDecimal("2000000.00"), result); // 총 200만원
        verify(limitRepository).findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.TRANSFER), eq(today));
        verify(limitRepository).save(any(DailyTransactionLimit.class));
    }

    @Test
    @DisplayName("이체 한도 초과 시 예외 발생 테스트")
    void checkAndUpdateLimit_TransferExceedLimit() {
        // Given
        when(limitRepository.findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.TRANSFER), eq(today)))
                .thenReturn(Optional.of(transferLimit));

        // When & Then
        assertThrows(TransactionException.class, () -> {
            limitService.checkAndUpdateLimit(
                    1L, 
                    TransactionType.TRANSFER, 
                    new BigDecimal("2500000.00"), // 100만원(기존) + 250만원(새로운) = 350만원 > 300만원(한도)
                    new BigDecimal("3000000.00")
            );
        });
        
        verify(limitRepository).findWithLockByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.TRANSFER), eq(today));
        verify(limitRepository, never()).save(any(DailyTransactionLimit.class));
    }

    @Test
    @DisplayName("오늘 사용량 조회 테스트")
    void getTodayUsage_Success() {
        // Given
        when(limitRepository.findByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.of(withdrawalLimit));

        // When
        BigDecimal result = limitService.getTodayUsage(1L, TransactionType.WITHDRAWAL);

        // Then
        assertEquals(new BigDecimal("300000.00"), result);
        verify(limitRepository).findByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today));
    }

    @Test
    @DisplayName("거래 내역 없는 경우 오늘 사용량 조회 테스트")
    void getTodayUsage_NoTransactions() {
        // Given
        when(limitRepository.findByAccountIdAndTypeAndDate(eq(3L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.empty());

        // When
        BigDecimal result = limitService.getTodayUsage(3L, TransactionType.WITHDRAWAL);

        // Then
        assertEquals(BigDecimal.ZERO, result);
        verify(limitRepository).findByAccountIdAndTypeAndDate(eq(3L), eq(TransactionType.WITHDRAWAL), eq(today));
    }

    @Test
    @DisplayName("남은 한도 조회 테스트")
    void getRemainingLimit_Success() {
        // Given
        when(limitRepository.findByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.of(withdrawalLimit));

        // When
        BigDecimal result = limitService.getRemainingLimit(
                1L, 
                TransactionType.WITHDRAWAL, 
                new BigDecimal("1000000.00")
        );

        // Then
        BigDecimal expected = new BigDecimal("1000000.00").subtract(new BigDecimal("300000.00"));
        assertEquals(expected, result);
        verify(limitRepository).findByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today));
    }

    @Test
    @DisplayName("한도 초과 시 남은 한도는 0 테스트")
    void getRemainingLimit_NoRemaining() {
        // Given
        DailyTransactionLimit fullLimit = DailyTransactionLimit.create(1L, TransactionType.WITHDRAWAL, today);
        fullLimit.setTotalAmount(new BigDecimal("1100000.00")); // 한도 초과
        
        when(limitRepository.findByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today)))
                .thenReturn(Optional.of(fullLimit));

        // When
        BigDecimal result = limitService.getRemainingLimit(
                1L, 
                TransactionType.WITHDRAWAL, 
                new BigDecimal("1000000.00")
        );

        // Then
        assertEquals(BigDecimal.ZERO, result); // 남은 한도 0
        verify(limitRepository).findByAccountIdAndTypeAndDate(eq(1L), eq(TransactionType.WITHDRAWAL), eq(today));
    }
}