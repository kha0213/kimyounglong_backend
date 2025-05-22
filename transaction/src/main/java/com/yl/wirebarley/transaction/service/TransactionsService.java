package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.account.api.AccountOperations;
import com.yl.wirebarley.account.exception.AccountException;
import com.yl.wirebarley.transaction.domain.TransactionType;
import com.yl.wirebarley.transaction.domain.Transactions;
import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.TransactionResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.exception.TransactionException;
import com.yl.wirebarley.transaction.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TransactionsService {
    @Value("${transaction.limit.daily.withdrawal}")
    private BigDecimal DAILY_WITHDRAWAL_LIMIT;
    @Value("${transaction.limit.daily.transfer}")
    private BigDecimal DAILY_TRANSFER_LIMIT;
    @Value("${transaction.fee.transfer}")
    private BigDecimal TRANSFER_FEE;

    private final TransactionsRepository transactionsRepository;
    private final AccountOperations accountOperations;
    private final TransactionLimitService limitService;

    public TransactionResponse deposit(DepositRequest request) {
        log.info("Processing deposit request: {}", request);
        
        AccountDto accountDto = getAccountById(request.getAccountId());

        Transactions transaction = Transactions.createForDeposit(request);
        
        try {
            AccountDto updatedAccount = accountOperations.updateBalance(
                    request.getAccountId(), 
                    request.getAmount()
            );
            
            transaction.markAsCompleted();
            Transactions savedTransaction = transactionsRepository.save(transaction);
            
            log.info("Deposit completed - accountId: {}, amount: {}, transactionId: {}", 
                    request.getAccountId(), request.getAmount(), savedTransaction.getId());
            
            return TransactionResponse.getResponse(savedTransaction, updatedAccount);
        } catch (Exception e) {
            log.error("Deposit failed - accountId: {}, amount: {}, error: {}", 
                    request.getAccountId(), request.getAmount(), e.getMessage(), e);
            
            transaction.markAsFailed();
            transactionsRepository.save(transaction);
            throw e;
        }
    }

    public TransactionResponse withdrawal(WithdrawalRequest request) {
        log.info("Processing withdrawal request: {}", request);
        
        AccountDto accountDto = getAccountById(request.getAccountId());
        
        if (accountDto.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Insufficient balance for withdrawal - accountId: {}, balance: {}, requestedAmount: {}", 
                    request.getAccountId(), accountDto.getBalance(), request.getAmount());
            throw TransactionException.insufficientBalance();
        }
        
        limitService.checkAndUpdateLimit(
                request.getAccountId(),
                TransactionType.WITHDRAWAL,
                request.getAmount(),
                DAILY_WITHDRAWAL_LIMIT
        );
        
        Transactions transaction = Transactions.createForWithdrawal(request);
        
        try {
            AccountDto updatedAccount = accountOperations.updateBalance(
                    request.getAccountId(), 
                    request.getAmount().negate()
            );
            
            transaction.markAsCompleted();
            Transactions savedTransaction = transactionsRepository.save(transaction);
            
            log.info("Withdrawal completed - accountId: {}, amount: {}, transactionId: {}", 
                    request.getAccountId(), request.getAmount(), savedTransaction.getId());
            
            return TransactionResponse.getResponse(savedTransaction, updatedAccount);
        } catch (Exception e) {
            log.error("Withdrawal failed - accountId: {}, amount: {}, error: {}", 
                    request.getAccountId(), request.getAmount(), e.getMessage(), e);
            
            transaction.markAsFailed();
            transactionsRepository.save(transaction);
            throw e;
        }
    }

    public TransactionResponse transfer(TransferRequest request) {
        log.info("Processing transfer request: {}", request);
        
        AccountDto sourceAccount = getAccountById(request.getAccountId());
        AccountDto targetAccount = getAccountById(request.getTargetAccountId());
        
        BigDecimal fee = request.getAmount().multiply(TRANSFER_FEE);
        
        BigDecimal totalAmount = request.getAmount().add(fee);
        
        if (sourceAccount.getBalance().compareTo(totalAmount) < 0) {
            log.warn("Insufficient balance for transfer - accountId: {}, balance: {}, totalAmount: {}", 
                    request.getAccountId(), sourceAccount.getBalance(), totalAmount);
            throw TransactionException.insufficientBalance();
        }
        
        limitService.checkAndUpdateLimit(
                request.getAccountId(),
                TransactionType.TRANSFER,
                request.getAmount(),  // 수수료 제외한 이체금액만 한도에 포함
                DAILY_TRANSFER_LIMIT
        );
        
        Transactions sourceTransaction = Transactions.createForTransfer(request, fee);
        Transactions targetTransaction = Transactions.createForTransferReceiver(request);
        
        try {
            AccountDto updatedSourceAccount = accountOperations.updateBalance(
                    sourceAccount.getId(), 
                    totalAmount.negate()
            );
            
            accountOperations.updateBalance(
                    targetAccount.getId(), 
                    request.getAmount()
            );
            
            sourceTransaction.markAsCompleted();
            targetTransaction.markAsCompleted();
            
            Transactions savedSourceTransaction = transactionsRepository.save(sourceTransaction);
            transactionsRepository.save(targetTransaction);
            
            log.info("Transfer completed - from: {}, to: {}, amount: {}, fee: {}, transactionId: {}", 
                    request.getAccountId(), request.getTargetAccountId(), 
                    request.getAmount(), fee, savedSourceTransaction.getId());
            
            return TransactionResponse.getResponse(savedSourceTransaction, updatedSourceAccount);
        } catch (Exception e) {
            log.error("Transfer failed - from: {}, to: {}, amount: {}, error: {}", 
                    request.getAccountId(), request.getTargetAccountId(), 
                    request.getAmount(), e.getMessage(), e);
            
            sourceTransaction.markAsFailed();
            targetTransaction.markAsFailed();
            transactionsRepository.save(sourceTransaction);
            transactionsRepository.save(targetTransaction);
            throw e;
        }
    }

    private AccountDto getAccountById(Long accountId) {
        return accountOperations.getAccount(accountId)
                .orElseThrow(() -> AccountException.accountNotFound(accountId));
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getRemainingWithdrawalLimit(Long accountId) {
        return limitService.getRemainingLimit(
                accountId, 
                TransactionType.WITHDRAWAL, 
                DAILY_WITHDRAWAL_LIMIT
        );
    }
    
    @Transactional(readOnly = true)
    public BigDecimal getRemainingTransferLimit(Long accountId) {
        return limitService.getRemainingLimit(
                accountId, 
                TransactionType.TRANSFER, 
                DAILY_TRANSFER_LIMIT
        );
    }
}