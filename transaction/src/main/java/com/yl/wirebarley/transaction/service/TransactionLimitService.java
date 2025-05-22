package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.transaction.domain.DailyTransactionLimit;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.exception.TransactionException;
import com.yl.wirebarley.transaction.repository.DailyTransactionLimitRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionLimitService {
    
    private final DailyTransactionLimitRepository limitRepository;
    
    @Transactional(propagation = Propagation.REQUIRED)
    public BigDecimal checkAndUpdateLimit(Long accountId, TransactionType type, BigDecimal amount, BigDecimal limitAmount) {
        log.info("Checking daily limit for account: {}, type: {}, amount: {}, limit: {}", 
                 accountId, type, amount, limitAmount);
        
        LocalDate today = LocalDate.now();
        
        // 비관적 락을 사용하여 동시성 이슈 방지
        DailyTransactionLimit limit = limitRepository.findWithLockByAccountIdAndTypeAndDate(accountId, type, today)
                .orElseGet(() -> DailyTransactionLimit.create(accountId, type, today));
        
        BigDecimal projectedTotal = limit.getTotalAmount().add(amount);
        
        // 일별 한도 초과 확인
        if (projectedTotal.compareTo(limitAmount) > 0) {
            log.warn("Daily limit exceeded for account: {}, type: {}, current: {}, amount: {}, limit: {}", 
                     accountId, type, limit.getTotalAmount(), amount, limitAmount);
            
            if (type == TransactionType.WITHDRAWAL) {
                throw TransactionException.dailyWithdrawalLimitExceeded();
            } else if (type == TransactionType.TRANSFER) {
                throw TransactionException.dailyTransferLimitExceeded();
            }
        }
        
        // 한도 내에 있으면 금액 추가
        limit.addAmount(amount);
        limitRepository.save(limit);
        
        log.info("Updated daily limit for account: {}, type: {}, total: {}", 
                 accountId, type, limit.getTotalAmount());
        
        return limit.getTotalAmount();
    }
    
    /**
     * 특정 계좌의 특정 거래 유형에 대한 오늘의 사용량 조회
     * 
     * @param accountId 계좌 ID
     * @param type 거래 유형
     * @return 오늘의 사용량 (아직 사용 내역이 없으면 0 반환)
     */
    @Transactional(readOnly = true)
    public BigDecimal getTodayUsage(Long accountId, TransactionType type) {
        LocalDate today = LocalDate.now();
        
        return limitRepository.findByAccountIdAndTypeAndDate(accountId, type, today)
                .map(DailyTransactionLimit::getTotalAmount)
                .orElse(BigDecimal.ZERO);
    }
    
    /**
     * 특정 계좌의 특정 거래 유형에 대한 남은 한도 조회
     * 
     * @param accountId 계좌 ID
     * @param type 거래 유형
     * @param limitAmount 총 한도 금액
     * @return 남은 한도 금액
     */
    @Transactional(readOnly = true)
    public BigDecimal getRemainingLimit(Long accountId, TransactionType type, BigDecimal limitAmount) {
        BigDecimal usedAmount = getTodayUsage(accountId, type);
        return limitAmount.subtract(usedAmount).max(BigDecimal.ZERO);
    }
}