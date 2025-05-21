package com.yl.wirebarley.transaction.service;

import com.yl.wirebarley.account.api.AccountDto;
import com.yl.wirebarley.account.api.AccountOperations;
import com.yl.wirebarley.account.exception.AccountException;
import com.yl.wirebarley.transaction.domain.Transactions;
import com.yl.wirebarley.transaction.domain.dto.DepositRequest;
import com.yl.wirebarley.transaction.domain.dto.TransactionResponse;
import com.yl.wirebarley.transaction.domain.dto.TransferRequest;
import com.yl.wirebarley.transaction.domain.dto.WithdrawalRequest;
import com.yl.wirebarley.transaction.exception.TransactionException;
import com.yl.wirebarley.transaction.repository.TransactionsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

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

    public TransactionResponse deposit(DepositRequest request) {
        getAccountById(request.getAccountId());

        Transactions transaction = Transactions.createForDeposit(request);

        try {
            AccountDto updatedAccount = accountOperations.updateBalance(
                    request.getAccountId(), 
                    request.getAmount()
            );
            
            transaction.markAsCompleted();
            Transactions savedTransaction = transactionsRepository.save(transaction);
            
            return TransactionResponse.getResponse(savedTransaction, updatedAccount);
        } catch (Exception e) {
            transaction.markAsFailed();
            transactionsRepository.save(transaction);
            throw e;
        }
    }

    public TransactionResponse withdrawal(WithdrawalRequest request) {
        AccountDto accountDto = getAccountById(request.getAccountId());
        
        if (accountDto.getBalance().compareTo(request.getAmount()) < 0) {
            throw TransactionException.insufficientBalance();
        }

        // TODO: 1일 출금 한도 체크

        Transactions transaction = Transactions.createForWithdrawal(request);
        
        try {
            AccountDto updatedAccount = accountOperations.updateBalance(
                    request.getAccountId(), 
                    request.getAmount().negate()
            );
            
            transaction.markAsCompleted();
            Transactions savedTransaction = transactionsRepository.save(transaction);
            
            return TransactionResponse.getResponse(savedTransaction, updatedAccount);
        } catch (Exception e) {
            transaction.markAsFailed();
            transactionsRepository.save(transaction);
            throw e;
        }
    }

    private AccountDto getAccountById(Long accountId) {
        return accountOperations.getAccount(accountId)
                .orElseThrow(() -> AccountException.accountNotFound(accountId));
    }

    public TransactionResponse transfer(TransferRequest request) {
        AccountDto sourceAccount = getAccountById(request.getAccountId());
        AccountDto targetAccount = getAccountById(request.getTargetAccountId());

        BigDecimal fee = request.getAmount().multiply(TRANSFER_FEE);
        BigDecimal totalAmount = request.getAmount().add(fee);
        
        if (sourceAccount.getBalance().compareTo(totalAmount) < 0) {
            throw TransactionException.insufficientBalance();
        }

        // TODO: 1일 이체 한도 체크
        
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
            
            return TransactionResponse.getResponse(savedSourceTransaction, updatedSourceAccount);
        } catch (Exception e) {
            sourceTransaction.markAsFailed();
            targetTransaction.markAsFailed();
            transactionsRepository.save(sourceTransaction);
            transactionsRepository.save(targetTransaction);
            throw e;
        }
    }
}
