package com.yl.wirebarley.transaction.repository;

import com.yl.wirebarley.transaction.domain.DailyTransactionLimit;
import com.yl.wirebarley.transaction.domain.TransactionType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyTransactionLimitRepository extends JpaRepository<DailyTransactionLimit, Long> {

    Optional<DailyTransactionLimit> findByAccountIdAndTypeAndDate(
            Long accountId, TransactionType type, LocalDate date);
    
    /**
     * 동시성 제어를 위한 비관적 락을 사용하여 일별 한도 정보 조회
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DailyTransactionLimit d WHERE d.accountId = :accountId AND d.type = :type AND d.date = :date")
    Optional<DailyTransactionLimit> findWithLockByAccountIdAndTypeAndDate(
            @Param("accountId") Long accountId, 
            @Param("type") TransactionType type, 
            @Param("date") LocalDate date);
}